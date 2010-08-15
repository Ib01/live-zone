
To clone one of these trees, install git, and run:
git clone git://android.git.kernel.org/ + project path.
To clone the entire platform, install repo, and run:
mkdir mydroid
cd mydroid
repo init -u git://android.git.kernel.org/platform/manifest.git
repo sync
For more information about git, see an overview, the tutorial or the man pages.

projects / platform/frameworks/base.git / blob
? search:  re


65f4194512fc0b61a1e9d37afbf9399385305a48
[platform/frameworks/base.git] / services / java / com / android / server / LocationManagerService.java
   1 /*
   2  * Copyright (C) 2007 The Android Open Source Project
   3  *
   4  * Licensed under the Apache License, Version 2.0 (the "License");
   5  * you may not use this file except in compliance with the License.
   6  * You may obtain a copy of the License at
   7  *
   8  *      http://www.apache.org/licenses/LICENSE-2.0
   9  *
  10  * Unless required by applicable law or agreed to in writing, software
  11  * distributed under the License is distributed on an "AS IS" BASIS,
  12  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  13  * See the License for the specific language governing permissions and
  14  * limitations under the License.
  15  */
  16 
  17 package com.android.server;
  18 
  19 import java.io.FileDescriptor;
  20 import java.io.PrintWriter;
  21 import java.util.ArrayList;
  22 import java.util.HashMap;
  23 import java.util.HashSet;
  24 import java.util.List;
  25 import java.util.Map;
  26 import java.util.Observable;
  27 import java.util.Observer;
  28 import java.util.Set;
  29 
  30 import android.app.Activity;
  31 import android.app.PendingIntent;
  32 import android.content.BroadcastReceiver;
  33 import android.content.ComponentName;
  34 import android.content.ContentQueryMap;
  35 import android.content.ContentResolver;
  36 import android.content.Context;
  37 import android.content.Intent;
  38 import android.content.IntentFilter;
  39 import android.content.ServiceConnection;
  40 import android.content.pm.PackageManager;
  41 import android.content.res.Resources;
  42 import android.database.Cursor;
  43 import android.location.Address;
  44 import android.location.GeocoderParams;
  45 import android.location.IGpsStatusListener;
  46 import android.location.IGpsStatusProvider;
  47 import android.location.ILocationListener;
  48 import android.location.ILocationManager;
  49 import android.location.INetInitiatedListener;
  50 import android.location.Location;
  51 import android.location.LocationManager;
  52 import android.location.LocationProvider;
  53 import android.location.LocationProviderInterface;
  54 import android.net.ConnectivityManager;
  55 import android.net.NetworkInfo;
  56 import android.net.Uri;
  57 import android.os.Binder;
  58 import android.os.Bundle;
  59 import android.os.Handler;
  60 import android.os.IBinder;
  61 import android.os.Looper;
  62 import android.os.Message;
  63 import android.os.PowerManager;
  64 import android.os.Process;
  65 import android.os.RemoteException;
  66 import android.provider.Settings;
  67 import android.util.Log;
  68 import android.util.Slog;
  69 import android.util.PrintWriterPrinter;
  70 
  71 import com.android.internal.location.GeocoderProxy;
  72 import com.android.internal.location.GpsLocationProvider;
  73 import com.android.internal.location.GpsNetInitiatedHandler;
  74 import com.android.internal.location.LocationProviderProxy;
  75 import com.android.internal.location.MockProvider;
  76 import com.android.internal.location.PassiveProvider;
  77 
  78 /**
  79  * The service class that manages LocationProviders and issues location
  80  * updates and alerts.
  81  *
  82  * {@hide}
  83  */
  84 public class LocationManagerService extends ILocationManager.Stub implements Runnable {
  85     private static final String TAG = "LocationManagerService";
  86     private static final boolean LOCAL_LOGV = false;
  87 
  88     // The last time a location was written, by provider name.
  89     private HashMap<String,Long> mLastWriteTime = new HashMap<String,Long>();
  90 
  91     private static final String ACCESS_FINE_LOCATION =
  92         android.Manifest.permission.ACCESS_FINE_LOCATION;
  93     private static final String ACCESS_COARSE_LOCATION =
  94         android.Manifest.permission.ACCESS_COARSE_LOCATION;
  95     private static final String ACCESS_MOCK_LOCATION =
  96         android.Manifest.permission.ACCESS_MOCK_LOCATION;
  97     private static final String ACCESS_LOCATION_EXTRA_COMMANDS =
  98         android.Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS;
  99     private static final String INSTALL_LOCATION_PROVIDER =
 100         android.Manifest.permission.INSTALL_LOCATION_PROVIDER;
 101 
 102     // Set of providers that are explicitly enabled
 103     private final Set<String> mEnabledProviders = new HashSet<String>();
 104 
 105     // Set of providers that are explicitly disabled
 106     private final Set<String> mDisabledProviders = new HashSet<String>();
 107 
 108     // Locations, status values, and extras for mock providers
 109     private final HashMap<String,MockProvider> mMockProviders = new HashMap<String,MockProvider>();
 110 
 111     private static boolean sProvidersLoaded = false;
 112 
 113     private final Context mContext;
 114     private GeocoderProxy mGeocodeProvider;
 115     private IGpsStatusProvider mGpsStatusProvider;
 116     private INetInitiatedListener mNetInitiatedListener;
 117     private LocationWorkerHandler mLocationHandler;
 118 
 119     // Cache the real providers for use in addTestProvider() and removeTestProvider()
 120      LocationProviderInterface mNetworkLocationProvider;
 121      LocationProviderInterface mGpsLocationProvider;
 122 
 123     // Handler messages
 124     private static final int MESSAGE_LOCATION_CHANGED = 1;
 125 
 126     // wakelock variables
 127     private final static String WAKELOCK_KEY = "LocationManagerService";
 128     private PowerManager.WakeLock mWakeLock = null;
 129     private int mPendingBroadcasts;
 130     
 131     /**
 132      * List of all receivers.
 133      */
 134     private final HashMap<Object, Receiver> mReceivers = new HashMap<Object, Receiver>();
 135 
 136 
 137     /**
 138      * List of location providers.
 139      */
 140     private final ArrayList<LocationProviderInterface> mProviders =
 141         new ArrayList<LocationProviderInterface>();
 142     private final HashMap<String, LocationProviderInterface> mProvidersByName
 143         = new HashMap<String, LocationProviderInterface>();
 144 
 145     /**
 146      * Object used internally for synchronization
 147      */
 148     private final Object mLock = new Object();
 149 
 150     /**
 151      * Mapping from provider name to all its UpdateRecords
 152      */
 153     private final HashMap<String,ArrayList<UpdateRecord>> mRecordsByProvider =
 154         new HashMap<String,ArrayList<UpdateRecord>>();
 155 
 156     // Proximity listeners
 157     private Receiver mProximityReceiver = null;
 158     private ILocationListener mProximityListener = null;
 159     private HashMap<PendingIntent,ProximityAlert> mProximityAlerts =
 160         new HashMap<PendingIntent,ProximityAlert>();
 161     private HashSet<ProximityAlert> mProximitiesEntered =
 162         new HashSet<ProximityAlert>();
 163 
 164     // Last known location for each provider
 165     private HashMap<String,Location> mLastKnownLocation =
 166         new HashMap<String,Location>();
 167 
 168     private int mNetworkState = LocationProvider.TEMPORARILY_UNAVAILABLE;
 169 
 170     // for Settings change notification
 171     private ContentQueryMap mSettings;
 172 
 173     /**
 174      * A wrapper class holding either an ILocationListener or a PendingIntent to receive
 175      * location updates.
 176      */
 177     private final class Receiver implements IBinder.DeathRecipient, PendingIntent.OnFinished {
 178         final ILocationListener mListener;
 179         final PendingIntent mPendingIntent;
 180         final Object mKey;
 181         final HashMap<String,UpdateRecord> mUpdateRecords = new HashMap<String,UpdateRecord>();
 182         int mPendingBroadcasts;
 183 
 184         Receiver(ILocationListener listener) {
 185             mListener = listener;
 186             mPendingIntent = null;
 187             mKey = listener.asBinder();
 188         }
 189 
 190         Receiver(PendingIntent intent) {
 191             mPendingIntent = intent;
 192             mListener = null;
 193             mKey = intent;
 194         }
 195 
 196         @Override
 197         public boolean equals(Object otherObj) {
 198             if (otherObj instanceof Receiver) {
 199                 return mKey.equals(
 200                         ((Receiver)otherObj).mKey);
 201             }
 202             return false;
 203         }
 204 
 205         @Override
 206         public int hashCode() {
 207             return mKey.hashCode();
 208         }
 209 
 210         @Override
 211         public String toString() {
 212             if (mListener != null) {
 213                 return "Receiver{"
 214                         + Integer.toHexString(System.identityHashCode(this))
 215                         + " Listener " + mKey + "}";
 216             } else {
 217                 return "Receiver{"
 218                         + Integer.toHexString(System.identityHashCode(this))
 219                         + " Intent " + mKey + "}";
 220             }
 221         }
 222 
 223         public boolean isListener() {
 224             return mListener != null;
 225         }
 226 
 227         public boolean isPendingIntent() {
 228             return mPendingIntent != null;
 229         }
 230 
 231         public ILocationListener getListener() {
 232             if (mListener != null) {
 233                 return mListener;
 234             }
 235             throw new IllegalStateException("Request for non-existent listener");
 236         }
 237 
 238         public PendingIntent getPendingIntent() {
 239             if (mPendingIntent != null) {
 240                 return mPendingIntent;
 241             }
 242             throw new IllegalStateException("Request for non-existent intent");
 243         }
 244 
 245         public boolean callStatusChangedLocked(String provider, int status, Bundle extras) {
 246             if (mListener != null) {
 247                 try {
 248                     synchronized (this) {
 249                         // synchronize to ensure incrementPendingBroadcastsLocked()
 250                         // is called before decrementPendingBroadcasts()
 251                         mListener.onStatusChanged(provider, status, extras);
 252                         if (mListener != mProximityListener) {
 253                             // call this after broadcasting so we do not increment
 254                             // if we throw an exeption.
 255                             incrementPendingBroadcastsLocked();
 256                         }
 257                     }
 258                 } catch (RemoteException e) {
 259                     return false;
 260                 }
 261             } else {
 262                 Intent statusChanged = new Intent();
 263                 statusChanged.putExtras(extras);
 264                 statusChanged.putExtra(LocationManager.KEY_STATUS_CHANGED, status);
 265                 try {
 266                     synchronized (this) {
 267                         // synchronize to ensure incrementPendingBroadcastsLocked()
 268                         // is called before decrementPendingBroadcasts()
 269                         mPendingIntent.send(mContext, 0, statusChanged, this, mLocationHandler);
 270                         // call this after broadcasting so we do not increment
 271                         // if we throw an exeption.
 272                         incrementPendingBroadcastsLocked();
 273                     }
 274                 } catch (PendingIntent.CanceledException e) {
 275                     return false;
 276                 }
 277             }
 278             return true;
 279         }
 280 
 281         public boolean callLocationChangedLocked(Location location) {
 282             if (mListener != null) {
 283                 try {
 284                     synchronized (this) {
 285                         // synchronize to ensure incrementPendingBroadcastsLocked()
 286                         // is called before decrementPendingBroadcasts()
 287                         mListener.onLocationChanged(location);
 288                         if (mListener != mProximityListener) {
 289                             // call this after broadcasting so we do not increment
 290                             // if we throw an exeption.
 291                             incrementPendingBroadcastsLocked();
 292                         }
 293                     }
 294                 } catch (RemoteException e) {
 295                     return false;
 296                 }
 297             } else {
 298                 Intent locationChanged = new Intent();
 299                 locationChanged.putExtra(LocationManager.KEY_LOCATION_CHANGED, location);
 300                 try {
 301                     synchronized (this) {
 302                         // synchronize to ensure incrementPendingBroadcastsLocked()
 303                         // is called before decrementPendingBroadcasts()
 304                         mPendingIntent.send(mContext, 0, locationChanged, this, mLocationHandler);
 305                         // call this after broadcasting so we do not increment
 306                         // if we throw an exeption.
 307                         incrementPendingBroadcastsLocked();
 308                     }
 309                 } catch (PendingIntent.CanceledException e) {
 310                     return false;
 311                 }
 312             }
 313             return true;
 314         }
 315 
 316         public boolean callProviderEnabledLocked(String provider, boolean enabled) {
 317             if (mListener != null) {
 318                 try {
 319                     synchronized (this) {
 320                         // synchronize to ensure incrementPendingBroadcastsLocked()
 321                         // is called before decrementPendingBroadcasts()
 322                         if (enabled) {
 323                             mListener.onProviderEnabled(provider);
 324                         } else {
 325                             mListener.onProviderDisabled(provider);
 326                         }
 327                         if (mListener != mProximityListener) {
 328                             // call this after broadcasting so we do not increment
 329                             // if we throw an exeption.
 330                             incrementPendingBroadcastsLocked();
 331                         }
 332                     }
 333                 } catch (RemoteException e) {
 334                     return false;
 335                 }
 336             } else {
 337                 Intent providerIntent = new Intent();
 338                 providerIntent.putExtra(LocationManager.KEY_PROVIDER_ENABLED, enabled);
 339                 try {
 340                     synchronized (this) {
 341                         // synchronize to ensure incrementPendingBroadcastsLocked()
 342                         // is called before decrementPendingBroadcasts()
 343                         mPendingIntent.send(mContext, 0, providerIntent, this, mLocationHandler);
 344                         // call this after broadcasting so we do not increment
 345                         // if we throw an exeption.
 346                         incrementPendingBroadcastsLocked();
 347                     }
 348                 } catch (PendingIntent.CanceledException e) {
 349                     return false;
 350                 }
 351             }
 352             return true;
 353         }
 354 
 355         public void binderDied() {
 356             if (LOCAL_LOGV) {
 357                 Slog.v(TAG, "Location listener died");
 358             }
 359             synchronized (mLock) {
 360                 removeUpdatesLocked(this);
 361             }
 362             synchronized (this) {
 363                 if (mPendingBroadcasts > 0) {
 364                     LocationManagerService.this.decrementPendingBroadcasts();
 365                     mPendingBroadcasts = 0;
 366                 }
 367             }
 368         }
 369 
 370         public void onSendFinished(PendingIntent pendingIntent, Intent intent,
 371                 int resultCode, String resultData, Bundle resultExtras) {
 372             synchronized (this) {
 373                 decrementPendingBroadcastsLocked();
 374             }
 375         }
 376 
 377         // this must be called while synchronized by caller in a synchronized block
 378         // containing the sending of the broadcaset
 379         private void incrementPendingBroadcastsLocked() {
 380             if (mPendingBroadcasts++ == 0) {
 381                 LocationManagerService.this.incrementPendingBroadcasts();
 382             }
 383         }
 384 
 385         private void decrementPendingBroadcastsLocked() {
 386             if (--mPendingBroadcasts == 0) {
 387                 LocationManagerService.this.decrementPendingBroadcasts();
 388             }
 389         }
 390     }
 391 
 392     public void locationCallbackFinished(ILocationListener listener) {
 393         //Do not use getReceiver here as that will add the ILocationListener to
 394         //the receiver list if it is not found.  If it is not found then the
 395         //LocationListener was removed when it had a pending broadcast and should
 396         //not be added back.
 397         IBinder binder = listener.asBinder();
 398         Receiver receiver = mReceivers.get(binder);
 399         if (receiver != null) {
 400             synchronized (receiver) {
 401                 // so wakelock calls will succeed
 402                 long identity = Binder.clearCallingIdentity();
 403                 receiver.decrementPendingBroadcastsLocked();
 404                 Binder.restoreCallingIdentity(identity);
 405            }
 406         }
 407     }
 408 
 409     private final class SettingsObserver implements Observer {
 410         public void update(Observable o, Object arg) {
 411             synchronized (mLock) {
 412                 updateProvidersLocked();
 413             }
 414         }
 415     }
 416 
 417     private void addProvider(LocationProviderInterface provider) {
 418         mProviders.add(provider);
 419         mProvidersByName.put(provider.getName(), provider);
 420     }
 421 
 422     private void removeProvider(LocationProviderInterface provider) {
 423         mProviders.remove(provider);
 424         mProvidersByName.remove(provider.getName());
 425     }
 426 
 427     private void loadProviders() {
 428         synchronized (mLock) {
 429             if (sProvidersLoaded) {
 430                 return;
 431             }
 432 
 433             // Load providers
 434             loadProvidersLocked();
 435             sProvidersLoaded = true;
 436         }
 437     }
 438 
 439     private void loadProvidersLocked() {
 440         try {
 441             _loadProvidersLocked();
 442         } catch (Exception e) {
 443             Slog.e(TAG, "Exception loading providers:", e);
 444         }
 445     }
 446 
 447     private void _loadProvidersLocked() {
 448         // Attempt to load "real" providers first
 449         if (GpsLocationProvider.isSupported()) {
 450             // Create a gps location provider
 451             GpsLocationProvider gpsProvider = new GpsLocationProvider(mContext, this);
 452             mGpsStatusProvider = gpsProvider.getGpsStatusProvider();
 453             mNetInitiatedListener = gpsProvider.getNetInitiatedListener();
 454             addProvider(gpsProvider);
 455             mGpsLocationProvider = gpsProvider;
 456         }
 457 
 458         // create a passive location provider, which is always enabled
 459         PassiveProvider passiveProvider = new PassiveProvider(this);
 460         addProvider(passiveProvider);
 461         mEnabledProviders.add(passiveProvider.getName());
 462 
 463         // initialize external network location and geocoder services
 464         Resources resources = mContext.getResources();
 465         String serviceName = resources.getString(
 466                 com.android.internal.R.string.config_networkLocationProvider);
 467         if (serviceName != null) {
 468             mNetworkLocationProvider =
 469                 new LocationProviderProxy(mContext, LocationManager.NETWORK_PROVIDER,
 470                         serviceName, mLocationHandler);
 471             addProvider(mNetworkLocationProvider);
 472         }
 473 
 474         serviceName = resources.getString(com.android.internal.R.string.config_geocodeProvider);
 475         if (serviceName != null) {
 476             mGeocodeProvider = new GeocoderProxy(mContext, serviceName);
 477         }
 478 
 479         updateProvidersLocked();
 480     }
 481 
 482     /**
 483      * @param context the context that the LocationManagerService runs in
 484      */
 485     public LocationManagerService(Context context) {
 486         super();
 487         mContext = context;
 488 
 489         if (LOCAL_LOGV) {
 490             Slog.v(TAG, "Constructed LocationManager Service");
 491         }
 492     }
 493 
 494     void systemReady() {
 495         // we defer starting up the service until the system is ready 
 496         Thread thread = new Thread(null, this, "LocationManagerService");
 497         thread.start();
 498     }
 499 
 500     private void initialize() {
 501         // Create a wake lock, needs to be done before calling loadProviders() below
 502         PowerManager powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
 503         mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKELOCK_KEY);
 504 
 505         // Load providers
 506         loadProviders();
 507 
 508         // Register for Network (Wifi or Mobile) updates
 509         IntentFilter intentFilter = new IntentFilter();
 510         intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
 511         // Register for Package Manager updates
 512         intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
 513         intentFilter.addAction(Intent.ACTION_PACKAGE_RESTARTED);
 514         intentFilter.addAction(Intent.ACTION_QUERY_PACKAGE_RESTART);
 515         mContext.registerReceiver(mBroadcastReceiver, intentFilter);
 516         IntentFilter sdFilter = new IntentFilter(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
 517         mContext.registerReceiver(mBroadcastReceiver, sdFilter);
 518 
 519         // listen for settings changes
 520         ContentResolver resolver = mContext.getContentResolver();
 521         Cursor settingsCursor = resolver.query(Settings.Secure.CONTENT_URI, null,
 522                 "(" + Settings.System.NAME + "=?)",
 523                 new String[]{Settings.Secure.LOCATION_PROVIDERS_ALLOWED},
 524                 null);
 525         mSettings = new ContentQueryMap(settingsCursor, Settings.System.NAME, true, mLocationHandler);
 526         SettingsObserver settingsObserver = new SettingsObserver();
 527         mSettings.addObserver(settingsObserver);
 528     }
 529 
 530     public void run()
 531     {
 532         Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
 533         Looper.prepare();
 534         mLocationHandler = new LocationWorkerHandler();
 535         initialize();
 536         Looper.loop();
 537     }
 538 
 539     private boolean isAllowedBySettingsLocked(String provider) {
 540         if (mEnabledProviders.contains(provider)) {
 541             return true;
 542         }
 543         if (mDisabledProviders.contains(provider)) {
 544             return false;
 545         }
 546         // Use system settings
 547         ContentResolver resolver = mContext.getContentResolver();
 548 
 549         return Settings.Secure.isLocationProviderEnabled(resolver, provider);
 550     }
 551 
 552     private void checkPermissionsSafe(String provider) {
 553         if ((LocationManager.GPS_PROVIDER.equals(provider)
 554                  || LocationManager.PASSIVE_PROVIDER.equals(provider))
 555             && (mContext.checkCallingOrSelfPermission(ACCESS_FINE_LOCATION)
 556                 != PackageManager.PERMISSION_GRANTED)) {
 557             throw new SecurityException("Requires ACCESS_FINE_LOCATION permission");
 558         }
 559         if (LocationManager.NETWORK_PROVIDER.equals(provider)
 560             && (mContext.checkCallingOrSelfPermission(ACCESS_FINE_LOCATION)
 561                 != PackageManager.PERMISSION_GRANTED)
 562             && (mContext.checkCallingOrSelfPermission(ACCESS_COARSE_LOCATION)
 563                 != PackageManager.PERMISSION_GRANTED)) {
 564             throw new SecurityException(
 565                 "Requires ACCESS_FINE_LOCATION or ACCESS_COARSE_LOCATION permission");
 566         }
 567     }
 568 
 569     private boolean isAllowedProviderSafe(String provider) {
 570         if ((LocationManager.GPS_PROVIDER.equals(provider)
 571                 || LocationManager.PASSIVE_PROVIDER.equals(provider))
 572             && (mContext.checkCallingOrSelfPermission(ACCESS_FINE_LOCATION)
 573                 != PackageManager.PERMISSION_GRANTED)) {
 574             return false;
 575         }
 576         if (LocationManager.NETWORK_PROVIDER.equals(provider)
 577             && (mContext.checkCallingOrSelfPermission(ACCESS_FINE_LOCATION)
 578                 != PackageManager.PERMISSION_GRANTED)
 579             && (mContext.checkCallingOrSelfPermission(ACCESS_COARSE_LOCATION)
 580                 != PackageManager.PERMISSION_GRANTED)) {
 581             return false;
 582         }
 583 
 584         return true;
 585     }
 586 
 587     public List<String> getAllProviders() {
 588         try {
 589             synchronized (mLock) {
 590                 return _getAllProvidersLocked();
 591             }
 592         } catch (SecurityException se) {
 593             throw se;
 594         } catch (Exception e) {
 595             Slog.e(TAG, "getAllProviders got exception:", e);
 596             return null;
 597         }
 598     }
 599 
 600     private List<String> _getAllProvidersLocked() {
 601         if (LOCAL_LOGV) {
 602             Slog.v(TAG, "getAllProviders");
 603         }
 604         ArrayList<String> out = new ArrayList<String>(mProviders.size());
 605         for (int i = mProviders.size() - 1; i >= 0; i--) {
 606             LocationProviderInterface p = mProviders.get(i);
 607             out.add(p.getName());
 608         }
 609         return out;
 610     }
 611 
 612     public List<String> getProviders(boolean enabledOnly) {
 613         try {
 614             synchronized (mLock) {
 615                 return _getProvidersLocked(enabledOnly);
 616             }
 617         } catch (SecurityException se) {
 618             throw se;
 619         } catch (Exception e) {
 620             Slog.e(TAG, "getProviders got exception:", e);
 621             return null;
 622         }
 623     }
 624 
 625     private List<String> _getProvidersLocked(boolean enabledOnly) {
 626         if (LOCAL_LOGV) {
 627             Slog.v(TAG, "getProviders");
 628         }
 629         ArrayList<String> out = new ArrayList<String>(mProviders.size());
 630         for (int i = mProviders.size() - 1; i >= 0; i--) {
 631             LocationProviderInterface p = mProviders.get(i);
 632             String name = p.getName();
 633             if (isAllowedProviderSafe(name)) {
 634                 if (enabledOnly && !isAllowedBySettingsLocked(name)) {
 635                     continue;
 636                 }
 637                 out.add(name);
 638             }
 639         }
 640         return out;
 641     }
 642 
 643     private void updateProvidersLocked() {
 644         for (int i = mProviders.size() - 1; i >= 0; i--) {
 645             LocationProviderInterface p = mProviders.get(i);
 646             boolean isEnabled = p.isEnabled();
 647             String name = p.getName();
 648             boolean shouldBeEnabled = isAllowedBySettingsLocked(name);
 649 
 650             if (isEnabled && !shouldBeEnabled) {
 651                 updateProviderListenersLocked(name, false);
 652             } else if (!isEnabled && shouldBeEnabled) {
 653                 updateProviderListenersLocked(name, true);
 654             }
 655 
 656         }
 657     }
 658 
 659     private void updateProviderListenersLocked(String provider, boolean enabled) {
 660         int listeners = 0;
 661 
 662         LocationProviderInterface p = mProvidersByName.get(provider);
 663         if (p == null) {
 664             return;
 665         }
 666 
 667         ArrayList<Receiver> deadReceivers = null;
 668         
 669         ArrayList<UpdateRecord> records = mRecordsByProvider.get(provider);
 670         if (records != null) {
 671             final int N = records.size();
 672             for (int i=0; i<N; i++) {
 673                 UpdateRecord record = records.get(i);
 674                 // Sends a notification message to the receiver
 675                 if (!record.mReceiver.callProviderEnabledLocked(provider, enabled)) {
 676                     if (deadReceivers == null) {
 677                         deadReceivers = new ArrayList<Receiver>();
 678                     }
 679                     deadReceivers.add(record.mReceiver);
 680                 }
 681                 listeners++;
 682             }
 683         }
 684 
 685         if (deadReceivers != null) {
 686             for (int i=deadReceivers.size()-1; i>=0; i--) {
 687                 removeUpdatesLocked(deadReceivers.get(i));
 688             }
 689         }
 690         
 691         if (enabled) {
 692             p.enable();
 693             if (listeners > 0) {
 694                 p.setMinTime(getMinTimeLocked(provider));
 695                 p.enableLocationTracking(true);
 696             }
 697         } else {
 698             p.enableLocationTracking(false);
 699             p.disable();
 700         }
 701     }
 702 
 703     private long getMinTimeLocked(String provider) {
 704         long minTime = Long.MAX_VALUE;
 705         ArrayList<UpdateRecord> records = mRecordsByProvider.get(provider);
 706         if (records != null) {
 707             for (int i=records.size()-1; i>=0; i--) {
 708                 minTime = Math.min(minTime, records.get(i).mMinTime);
 709             }
 710         }
 711         return minTime;
 712     }
 713 
 714     private class UpdateRecord {
 715         final String mProvider;
 716         final Receiver mReceiver;
 717         final long mMinTime;
 718         final float mMinDistance;
 719         final int mUid;
 720         Location mLastFixBroadcast;
 721         long mLastStatusBroadcast;
 722 
 723         /**
 724          * Note: must be constructed with lock held.
 725          */
 726         UpdateRecord(String provider, long minTime, float minDistance,
 727             Receiver receiver, int uid) {
 728             mProvider = provider;
 729             mReceiver = receiver;
 730             mMinTime = minTime;
 731             mMinDistance = minDistance;
 732             mUid = uid;
 733 
 734             ArrayList<UpdateRecord> records = mRecordsByProvider.get(provider);
 735             if (records == null) {
 736                 records = new ArrayList<UpdateRecord>();
 737                 mRecordsByProvider.put(provider, records);
 738             }
 739             if (!records.contains(this)) {
 740                 records.add(this);
 741             }
 742         }
 743 
 744         /**
 745          * Method to be called when a record will no longer be used.  Calling this multiple times
 746          * must have the same effect as calling it once.
 747          */
 748         void disposeLocked() {
 749             ArrayList<UpdateRecord> records = mRecordsByProvider.get(this.mProvider);
 750             if (records != null) {
 751                 records.remove(this);
 752             }
 753         }
 754 
 755         @Override
 756         public String toString() {
 757             return "UpdateRecord{"
 758                     + Integer.toHexString(System.identityHashCode(this))
 759                     + " " + mProvider + " " + mReceiver + "}";
 760         }
 761         
 762         void dump(PrintWriter pw, String prefix) {
 763             pw.println(prefix + this);
 764             pw.println(prefix + "mProvider=" + mProvider + " mReceiver=" + mReceiver);
 765             pw.println(prefix + "mMinTime=" + mMinTime + " mMinDistance=" + mMinDistance);
 766             pw.println(prefix + "mUid=" + mUid);
 767             pw.println(prefix + "mLastFixBroadcast:");
 768             if (mLastFixBroadcast != null) {
 769                 mLastFixBroadcast.dump(new PrintWriterPrinter(pw), prefix + "  ");
 770             }
 771             pw.println(prefix + "mLastStatusBroadcast=" + mLastStatusBroadcast);
 772         }
 773     }
 774 
 775     private Receiver getReceiver(ILocationListener listener) {
 776         IBinder binder = listener.asBinder();
 777         Receiver receiver = mReceivers.get(binder);
 778         if (receiver == null) {
 779             receiver = new Receiver(listener);
 780             mReceivers.put(binder, receiver);
 781 
 782             try {
 783                 if (receiver.isListener()) {
 784                     receiver.getListener().asBinder().linkToDeath(receiver, 0);
 785                 }
 786             } catch (RemoteException e) {
 787                 Slog.e(TAG, "linkToDeath failed:", e);
 788                 return null;
 789             }
 790         }
 791         return receiver;
 792     }
 793 
 794     private Receiver getReceiver(PendingIntent intent) {
 795         Receiver receiver = mReceivers.get(intent);
 796         if (receiver == null) {
 797             receiver = new Receiver(intent);
 798             mReceivers.put(intent, receiver);
 799         }
 800         return receiver;
 801     }
 802 
 803     private boolean providerHasListener(String provider, int uid, Receiver excludedReceiver) {
 804         ArrayList<UpdateRecord> records = mRecordsByProvider.get(provider);
 805         if (records != null) {
 806             for (int i = records.size() - 1; i >= 0; i--) {
 807                 UpdateRecord record = records.get(i);
 808                 if (record.mUid == uid && record.mReceiver != excludedReceiver) {
 809                     return true;
 810                 }
 811            }
 812         }
 813         for (ProximityAlert alert : mProximityAlerts.values()) {
 814             if (alert.mUid == uid) {
 815                 return true;
 816             }
 817         }
 818         return false;
 819     }
 820 
 821     public void requestLocationUpdates(String provider,
 822         long minTime, float minDistance, ILocationListener listener) {
 823 
 824         try {
 825             synchronized (mLock) {
 826                 requestLocationUpdatesLocked(provider, minTime, minDistance, getReceiver(listener));
 827             }
 828         } catch (SecurityException se) {
 829             throw se;
 830         } catch (IllegalArgumentException iae) {
 831             throw iae;
 832         } catch (Exception e) {
 833             Slog.e(TAG, "requestUpdates got exception:", e);
 834         }
 835     }
 836 
 837     public void requestLocationUpdatesPI(String provider,
 838             long minTime, float minDistance, PendingIntent intent) {
 839         try {
 840             synchronized (mLock) {
 841                 requestLocationUpdatesLocked(provider, minTime, minDistance, getReceiver(intent));
 842             }
 843         } catch (SecurityException se) {
 844             throw se;
 845         } catch (IllegalArgumentException iae) {
 846             throw iae;
 847         } catch (Exception e) {
 848             Slog.e(TAG, "requestUpdates got exception:", e);
 849         }
 850     }
 851 
 852     private void requestLocationUpdatesLocked(String provider, long minTime, float minDistance, Receiver receiver) {
 854         if (LOCAL_LOGV) {
 855             Slog.v(TAG, "_requestLocationUpdates: listener = " + receiver);
 856         }
 857 
 858         LocationProviderInterface p = mProvidersByName.get(provider);
 859         if (p == null) {
 860             throw new IllegalArgumentException("provider=" + provider);
 861         }
 862 
 863         checkPermissionsSafe(provider);
 864 
 865         // so wakelock calls will succeed
 866         final int callingUid = Binder.getCallingUid();
 867         boolean newUid = !providerHasListener(provider, callingUid, null);
 868         long identity = Binder.clearCallingIdentity();
 869         try {
 870             UpdateRecord r = new UpdateRecord(provider, minTime, minDistance, receiver, callingUid);
 871             UpdateRecord oldRecord = receiver.mUpdateRecords.put(provider, r);
 872             if (oldRecord != null) {
 873                 oldRecord.disposeLocked();
 874             }
 875 
 876             if (newUid) {
 877                 p.addListener(callingUid);
 878             }
 879 
 880             boolean isProviderEnabled = isAllowedBySettingsLocked(provider);
 881             if (isProviderEnabled) {
 882                 long minTimeForProvider = getMinTimeLocked(provider);
 883                 p.setMinTime(minTimeForProvider);
 884                 p.enableLocationTracking(true);
 885             } else {
 886                 // Notify the listener that updates are currently disabled
 887                 receiver.callProviderEnabledLocked(provider, false);
 888             }
 889         } finally {
 890             Binder.restoreCallingIdentity(identity);
 891         }
 892     }
 893 
 894     public void removeUpdates(ILocationListener listener) {
 895         try {
 896             synchronized (mLock) {
 897                 removeUpdatesLocked(getReceiver(listener));
 898             }
 899         } catch (SecurityException se) {
 900             throw se;
 901         } catch (IllegalArgumentException iae) {
 902             throw iae;
 903         } catch (Exception e) {
 904             Slog.e(TAG, "removeUpdates got exception:", e);
 905         }
 906     }
 907 
 908     public void removeUpdatesPI(PendingIntent intent) {
 909         try {
 910             synchronized (mLock) {
 911                 removeUpdatesLocked(getReceiver(intent));
 912             }
 913         } catch (SecurityException se) {
 914             throw se;
 915         } catch (IllegalArgumentException iae) {
 916             throw iae;
 917         } catch (Exception e) {
 918             Slog.e(TAG, "removeUpdates got exception:", e);
 919         }
 920     }
 921 
 922     private void removeUpdatesLocked(Receiver receiver) {
 923         if (LOCAL_LOGV) {
 924             Slog.v(TAG, "_removeUpdates: listener = " + receiver);
 925         }
 926 
 927         // so wakelock calls will succeed
 928         final int callingUid = Binder.getCallingUid();
 929         long identity = Binder.clearCallingIdentity();
 930         try {
 931             if (mReceivers.remove(receiver.mKey) != null && receiver.isListener()) {
 932                 receiver.getListener().asBinder().unlinkToDeath(receiver, 0);
 933                 synchronized(receiver) {
 934                     if(receiver.mPendingBroadcasts > 0) {
 935                         decrementPendingBroadcasts();
 936                         receiver.mPendingBroadcasts = 0;
 937                     }
 938                 }
 939             }
 940 
 941             // Record which providers were associated with this listener
 942             HashSet<String> providers = new HashSet<String>();
 943             HashMap<String,UpdateRecord> oldRecords = receiver.mUpdateRecords;
 944             if (oldRecords != null) {
 945                 // Call dispose() on the obsolete update records.
 946                 for (UpdateRecord record : oldRecords.values()) {
 947                     if (!providerHasListener(record.mProvider, callingUid, receiver)) {
 948                         LocationProviderInterface p = mProvidersByName.get(record.mProvider);
 949                         if (p != null) {
 950                             p.removeListener(callingUid);
 951                         }
 952                     }
 953                     record.disposeLocked();
 954                 }
 955                 // Accumulate providers
 956                 providers.addAll(oldRecords.keySet());
 957             }
 958 
 959             // See if the providers associated with this listener have any
 960             // other listeners; if one does, inform it of the new smallest minTime
 961             // value; if one does not, disable location tracking for it
 962             for (String provider : providers) {
 963                 // If provider is already disabled, don't need to do anything
 964                 if (!isAllowedBySettingsLocked(provider)) {
 965                     continue;
 966                 }
 967 
 968                 boolean hasOtherListener = false;
 969                 ArrayList<UpdateRecord> recordsForProvider = mRecordsByProvider.get(provider);
 970                 if (recordsForProvider != null && recordsForProvider.size() > 0) {
 971                     hasOtherListener = true;
 972                 }
 973 
 974                 LocationProviderInterface p = mProvidersByName.get(provider);
 975                 if (p != null) {
 976                     if (hasOtherListener) {
 977                         p.setMinTime(getMinTimeLocked(provider));
 978                     } else {
 979                         p.enableLocationTracking(false);
 980                     }
 981                 }
 982             }
 983         } finally {
 984             Binder.restoreCallingIdentity(identity);
 985         }
 986     }
 987 
 988     public boolean addGpsStatusListener(IGpsStatusListener listener) {
 989         if (mGpsStatusProvider == null) {
 990             return false;
 991         }
 992         if (mContext.checkCallingOrSelfPermission(ACCESS_FINE_LOCATION) !=
 993                 PackageManager.PERMISSION_GRANTED) {
 994             throw new SecurityException("Requires ACCESS_FINE_LOCATION permission");
 995         }
 996 
 997         try {
 998             mGpsStatusProvider.addGpsStatusListener(listener);
 999         } catch (RemoteException e) {
1000             Slog.e(TAG, "mGpsStatusProvider.addGpsStatusListener failed", e);
1001             return false;
1002         }
1003         return true;
1004     }
1005 
1006     public void removeGpsStatusListener(IGpsStatusListener listener) {
1007         synchronized (mLock) {
1008             try {
1009                 mGpsStatusProvider.removeGpsStatusListener(listener);
1010             } catch (Exception e) {
1011                 Slog.e(TAG, "mGpsStatusProvider.removeGpsStatusListener failed", e);
1012             }
1013         }
1014     }
1015 
1016     public boolean sendExtraCommand(String provider, String command, Bundle extras) {
1017         if (provider == null) {
1018             // throw NullPointerException to remain compatible with previous implementation
1019             throw new NullPointerException();
1020         }
1021 
1022         // first check for permission to the provider
1023         checkPermissionsSafe(provider);
1024         // and check for ACCESS_LOCATION_EXTRA_COMMANDS
1025         if ((mContext.checkCallingOrSelfPermission(ACCESS_LOCATION_EXTRA_COMMANDS)
1026                 != PackageManager.PERMISSION_GRANTED)) {
1027             throw new SecurityException("Requires ACCESS_LOCATION_EXTRA_COMMANDS permission");
1028         }
1029 
1030         synchronized (mLock) {
1031             LocationProviderInterface p = mProvidersByName.get(provider);
1032             if (p == null) {
1033                 return false;
1034             }
1035     
1036             return p.sendExtraCommand(command, extras);
1037         }
1038     }
1039 
1040     public boolean sendNiResponse(int notifId, int userResponse)
1041     {
1042         if (Binder.getCallingUid() != Process.myUid()) {
1043             throw new SecurityException(
1044                     "calling sendNiResponse from outside of the system is not allowed");
1045         }
1046         try {
1047             return mNetInitiatedListener.sendNiResponse(notifId, userResponse);
1048         }
1049         catch (RemoteException e)
1050         {
1051             Slog.e(TAG, "RemoteException in LocationManagerService.sendNiResponse");
1052             return false;
1053         }
1054     }
1055 
1056     class ProximityAlert {
1057         final int  mUid;
1058         final double mLatitude;
1059         final double mLongitude;
1060         final float mRadius;
1061         final long mExpiration;
1062         final PendingIntent mIntent;
1063         final Location mLocation;
1064 
1065         public ProximityAlert(int uid, double latitude, double longitude,
1066             float radius, long expiration, PendingIntent intent) {
1067             mUid = uid;
1068             mLatitude = latitude;
1069             mLongitude = longitude;
1070             mRadius = radius;
1071             mExpiration = expiration;
1072             mIntent = intent;
1073 
1074             mLocation = new Location("");
1075             mLocation.setLatitude(latitude);
1076             mLocation.setLongitude(longitude);
1077         }
1078 
1079         long getExpiration() {
1080             return mExpiration;
1081         }
1082 
1083         PendingIntent getIntent() {
1084             return mIntent;
1085         }
1086 
1087         boolean isInProximity(double latitude, double longitude, float accuracy) {
1088             Location loc = new Location("");
1089             loc.setLatitude(latitude);
1090             loc.setLongitude(longitude);
1091 
1092             double radius = loc.distanceTo(mLocation);
1093             return radius <= Math.max(mRadius,accuracy);
1094         }
1095         
1096         @Override
1097         public String toString() {
1098             return "ProximityAlert{"
1099                     + Integer.toHexString(System.identityHashCode(this))
1100                     + " uid " + mUid + mIntent + "}";
1101         }
1102         
1103         void dump(PrintWriter pw, String prefix) {
1104             pw.println(prefix + this);
1105             pw.println(prefix + "mLatitude=" + mLatitude + " mLongitude=" + mLongitude);
1106             pw.println(prefix + "mRadius=" + mRadius + " mExpiration=" + mExpiration);
1107             pw.println(prefix + "mIntent=" + mIntent);
1108             pw.println(prefix + "mLocation:");
1109             mLocation.dump(new PrintWriterPrinter(pw), prefix + "  ");
1110         }
1111     }
1112 
1113     // Listener for receiving locations to trigger proximity alerts
1114     class ProximityListener extends ILocationListener.Stub implements PendingIntent.OnFinished {
1115 
1116         boolean isGpsAvailable = false;
1117 
1118         // Note: this is called with the lock held.
1119         public void onLocationChanged(Location loc) {
1120 
1121             // If Gps is available, then ignore updates from NetworkLocationProvider
1122             if (loc.getProvider().equals(LocationManager.GPS_PROVIDER)) {
1123                 isGpsAvailable = true;
1124             }
1125             if (isGpsAvailable && loc.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
1126                 return;
1127             }
1128 
1129             // Process proximity alerts
1130             long now = System.currentTimeMillis();
1131             double latitude = loc.getLatitude();
1132             double longitude = loc.getLongitude();
1133             float accuracy = loc.getAccuracy();
1134             ArrayList<PendingIntent> intentsToRemove = null;
1135 
1136             for (ProximityAlert alert : mProximityAlerts.values()) {
1137                 PendingIntent intent = alert.getIntent();
1138                 long expiration = alert.getExpiration();
1139 
1140                 if ((expiration == -1) || (now <= expiration)) {
1141                     boolean entered = mProximitiesEntered.contains(alert);
1142                     boolean inProximity =
1143                         alert.isInProximity(latitude, longitude, accuracy);
1144                     if (!entered && inProximity) {
1145                         if (LOCAL_LOGV) {
1146                             Slog.v(TAG, "Entered alert");
1147                         }
1148                         mProximitiesEntered.add(alert);
1149                         Intent enteredIntent = new Intent();
1150                         enteredIntent.putExtra(LocationManager.KEY_PROXIMITY_ENTERING, true);
1151                         try {
1152                             synchronized (this) {
1153                                 // synchronize to ensure incrementPendingBroadcasts()
1154                                 // is called before decrementPendingBroadcasts()
1155                                 intent.send(mContext, 0, enteredIntent, this, mLocationHandler);
1156                                 // call this after broadcasting so we do not increment
1157                                 // if we throw an exeption.
1158                                 incrementPendingBroadcasts();
1159                             }
1160                         } catch (PendingIntent.CanceledException e) {
1161                             if (LOCAL_LOGV) {
1162                                 Slog.v(TAG, "Canceled proximity alert: " + alert, e);
1163                             }
1164                             if (intentsToRemove == null) {
1165                                 intentsToRemove = new ArrayList<PendingIntent>();
1166                             }
1167                             intentsToRemove.add(intent);
1168                         }
1169                     } else if (entered && !inProximity) {
1170                         if (LOCAL_LOGV) {
1171                             Slog.v(TAG, "Exited alert");
1172                         }
1173                         mProximitiesEntered.remove(alert);
1174                         Intent exitedIntent = new Intent();
1175                         exitedIntent.putExtra(LocationManager.KEY_PROXIMITY_ENTERING, false);
1176                         try {
1177                             synchronized (this) {
1178                                 // synchronize to ensure incrementPendingBroadcasts()
1179                                 // is called before decrementPendingBroadcasts()
1180                                 intent.send(mContext, 0, exitedIntent, this, mLocationHandler);
1181                                 // call this after broadcasting so we do not increment
1182                                 // if we throw an exeption.
1183                                 incrementPendingBroadcasts();
1184                             }
1185                         } catch (PendingIntent.CanceledException e) {
1186                             if (LOCAL_LOGV) {
1187                                 Slog.v(TAG, "Canceled proximity alert: " + alert, e);
1188                             }
1189                             if (intentsToRemove == null) {
1190                                 intentsToRemove = new ArrayList<PendingIntent>();
1191                             }
1192                             intentsToRemove.add(intent);
1193                         }
1194                     }
1195                 } else {
1196                     // Mark alert for expiration
1197                     if (LOCAL_LOGV) {
1198                         Slog.v(TAG, "Expiring proximity alert: " + alert);
1199                     }
1200                     if (intentsToRemove == null) {
1201                         intentsToRemove = new ArrayList<PendingIntent>();
1202                     }
1203                     intentsToRemove.add(alert.getIntent());
1204                 }
1205             }
1206 
1207             // Remove expired alerts
1208             if (intentsToRemove != null) {
1209                 for (PendingIntent i : intentsToRemove) {
1210                     ProximityAlert alert = mProximityAlerts.get(i);
1211                     mProximitiesEntered.remove(alert);
1212                     removeProximityAlertLocked(i);
1213                 }
1214             }
1215         }
1216 
1217         // Note: this is called with the lock held.
1218         public void onProviderDisabled(String provider) {
1219             if (provider.equals(LocationManager.GPS_PROVIDER)) {
1220                 isGpsAvailable = false;
1221             }
1222         }
1223 
1224         // Note: this is called with the lock held.
1225         public void onProviderEnabled(String provider) {
1226             // ignore
1227         }
1228 
1229         // Note: this is called with the lock held.
1230         public void onStatusChanged(String provider, int status, Bundle extras) {
1231             if ((provider.equals(LocationManager.GPS_PROVIDER)) &&
1232                 (status != LocationProvider.AVAILABLE)) {
1233                 isGpsAvailable = false;
1234             }
1235         }
1236 
1237         public void onSendFinished(PendingIntent pendingIntent, Intent intent,
1238                 int resultCode, String resultData, Bundle resultExtras) {
1239             // synchronize to ensure incrementPendingBroadcasts()
1240             // is called before decrementPendingBroadcasts()
1241             synchronized (this) {
1242                 decrementPendingBroadcasts();
1243             }
1244         }
1245     }
1246 
1247     public void addProximityAlert(double latitude, double longitude,
1248         float radius, long expiration, PendingIntent intent) {
1249         try {
1250             synchronized (mLock) {
1251                 addProximityAlertLocked(latitude, longitude, radius, expiration, intent);
1252             }
1253         } catch (SecurityException se) {
1254             throw se;
1255         } catch (IllegalArgumentException iae) {
1256             throw iae;
1257         } catch (Exception e) {
1258             Slog.e(TAG, "addProximityAlert got exception:", e);
1259         }
1260     }
1261 
1262     private void addProximityAlertLocked(double latitude, double longitude,
1263         float radius, long expiration, PendingIntent intent) {
1264         if (LOCAL_LOGV) {
1265             Slog.v(TAG, "addProximityAlert: latitude = " + latitude +
1266                     ", longitude = " + longitude +
1267                     ", expiration = " + expiration +
1268                     ", intent = " + intent);
1269         }
1270 
1271         // Require ability to access all providers for now
1272         if (!isAllowedProviderSafe(LocationManager.GPS_PROVIDER) ||
1273             !isAllowedProviderSafe(LocationManager.NETWORK_PROVIDER)) {
1274             throw new SecurityException("Requires ACCESS_FINE_LOCATION permission");
1275         }
1276 
1277         if (expiration != -1) {
1278             expiration += System.currentTimeMillis();
1279         }
1280         ProximityAlert alert = new ProximityAlert(Binder.getCallingUid(),
1281                 latitude, longitude, radius, expiration, intent);
1282         mProximityAlerts.put(intent, alert);
1283 
1284         if (mProximityReceiver == null) {
1285             mProximityListener = new ProximityListener();
1286             mProximityReceiver = new Receiver(mProximityListener);
1287 
1288             for (int i = mProviders.size() - 1; i >= 0; i--) {
1289                 LocationProviderInterface provider = mProviders.get(i);
1290                 requestLocationUpdatesLocked(provider.getName(), 1000L, 1.0f, mProximityReceiver);
1291             }
1292         }
1293     }
1294 
1295     public void removeProximityAlert(PendingIntent intent) {
1296         try {
1297             synchronized (mLock) {
1298                removeProximityAlertLocked(intent);
1299             }
1300         } catch (SecurityException se) {
1301             throw se;
1302         } catch (IllegalArgumentException iae) {
1303             throw iae;
1304         } catch (Exception e) {
1305             Slog.e(TAG, "removeProximityAlert got exception:", e);
1306         }
1307     }
1308 
1309     private void removeProximityAlertLocked(PendingIntent intent) {
1310         if (LOCAL_LOGV) {
1311             Slog.v(TAG, "removeProximityAlert: intent = " + intent);
1312         }
1313 
1314         mProximityAlerts.remove(intent);
1315         if (mProximityAlerts.size() == 0) {
1316             removeUpdatesLocked(mProximityReceiver);
1317             mProximityReceiver = null;
1318             mProximityListener = null;
1319         }
1320      }
1321 
1322     /**
1323      * @return null if the provider does not exist
1324      * @throws SecurityException if the provider is not allowed to be
1325      * accessed by the caller
1326      */
1327     public Bundle getProviderInfo(String provider) {
1328         try {
1329             synchronized (mLock) {
1330                 return _getProviderInfoLocked(provider);
1331             }
1332         } catch (SecurityException se) {
1333             throw se;
1334         } catch (IllegalArgumentException iae) {
1335             throw iae;
1336         } catch (Exception e) {
1337             Slog.e(TAG, "_getProviderInfo got exception:", e);
1338             return null;
1339         }
1340     }
1341 
1342     private Bundle _getProviderInfoLocked(String provider) {
1343         LocationProviderInterface p = mProvidersByName.get(provider);
1344         if (p == null) {
1345             return null;
1346         }
1347 
1348         checkPermissionsSafe(provider);
1349 
1350         Bundle b = new Bundle();
1351         b.putBoolean("network", p.requiresNetwork());
1352         b.putBoolean("satellite", p.requiresSatellite());
1353         b.putBoolean("cell", p.requiresCell());
1354         b.putBoolean("cost", p.hasMonetaryCost());
1355         b.putBoolean("altitude", p.supportsAltitude());
1356         b.putBoolean("speed", p.supportsSpeed());
1357         b.putBoolean("bearing", p.supportsBearing());
1358         b.putInt("power", p.getPowerRequirement());
1359         b.putInt("accuracy", p.getAccuracy());
1360 
1361         return b;
1362     }
1363 
1364     public boolean isProviderEnabled(String provider) {
1365         try {
1366             synchronized (mLock) {
1367                 return _isProviderEnabledLocked(provider);
1368             }
1369         } catch (SecurityException se) {
1370             throw se;
1371         } catch (IllegalArgumentException iae) {
1372             throw iae;
1373         } catch (Exception e) {
1374             Slog.e(TAG, "isProviderEnabled got exception:", e);
1375             return false;
1376         }
1377     }
1378 
1379     public void reportLocation(Location location, boolean passive) {
1380         if (mContext.checkCallingOrSelfPermission(INSTALL_LOCATION_PROVIDER)
1381                 != PackageManager.PERMISSION_GRANTED) {
1382             throw new SecurityException("Requires INSTALL_LOCATION_PROVIDER permission");
1383         }
1384 
1385         mLocationHandler.removeMessages(MESSAGE_LOCATION_CHANGED, location);
1386         Message m = Message.obtain(mLocationHandler, MESSAGE_LOCATION_CHANGED, location);
1387         m.arg1 = (passive ? 1 : 0);
1388         mLocationHandler.sendMessageAtFrontOfQueue(m);
1389     }
1390 
1391     private boolean _isProviderEnabledLocked(String provider) {
1392         checkPermissionsSafe(provider);
1393 
1394         LocationProviderInterface p = mProvidersByName.get(provider);
1395         if (p == null) {
1396             throw new IllegalArgumentException("provider=" + provider);
1397         }
1398         return isAllowedBySettingsLocked(provider);
1399     }
1400 
1401     public Location getLastKnownLocation(String provider) {
1402         try {
1403             synchronized (mLock) {
1404                 return _getLastKnownLocationLocked(provider);
1405             }
1406         } catch (SecurityException se) {
1407             throw se;
1408         } catch (IllegalArgumentException iae) {
1409             throw iae;
1410         } catch (Exception e) {
1411             Slog.e(TAG, "getLastKnownLocation got exception:", e);
1412             return null;
1413         }
1414     }
1415 
1416     private Location _getLastKnownLocationLocked(String provider) {
1417         checkPermissionsSafe(provider);
1418 
1419         LocationProviderInterface p = mProvidersByName.get(provider);
1420         if (p == null) {
1421             throw new IllegalArgumentException("provider=" + provider);
1422         }
1423 
1424         if (!isAllowedBySettingsLocked(provider)) {
1425             return null;
1426         }
1427 
1428         return mLastKnownLocation.get(provider);
1429     }
1430 
1431     private static boolean shouldBroadcastSafe(Location loc, Location lastLoc, UpdateRecord record) {
1432         // Always broadcast the first update
1433         if (lastLoc == null) {
1434             return true;
1435         }
1436 
1437         // Don't broadcast same location again regardless of condition
1438         // TODO - we should probably still rebroadcast if user explicitly sets a minTime > 0
1439         if (loc.getTime() == lastLoc.getTime()) {
1440             return false;
1441         }
1442 
1443         // Check whether sufficient distance has been traveled
1444         double minDistance = record.mMinDistance;
1445         if (minDistance > 0.0) {
1446             if (loc.distanceTo(lastLoc) <= minDistance) {
1447                 return false;
1448             }
1449         }
1450 
1451         return true;
1452     }
1453 
1454     private void handleLocationChangedLocked(Location location, boolean passive) {
1455         String provider = (passive ? LocationManager.PASSIVE_PROVIDER : location.getProvider());
1456         ArrayList<UpdateRecord> records = mRecordsByProvider.get(provider);
1457         if (records == null || records.size() == 0) {
1458             return;
1459         }
1460 
1461         LocationProviderInterface p = mProvidersByName.get(provider);
1462         if (p == null) {
1463             return;
1464         }
1465 
1466         // Update last known location for provider
1467         Location lastLocation = mLastKnownLocation.get(provider);
1468         if (lastLocation == null) {
1469             mLastKnownLocation.put(provider, new Location(location));
1470         } else {
1471             lastLocation.set(location);
1472         }
1473 
1474         // Fetch latest status update time
1475         long newStatusUpdateTime = p.getStatusUpdateTime();
1476 
1477        // Get latest status
1478         Bundle extras = new Bundle();
1479         int status = p.getStatus(extras);
1480 
1481         ArrayList<Receiver> deadReceivers = null;
1482         
1483         // Broadcast location or status to all listeners
1484         final int N = records.size();
1485         for (int i=0; i<N; i++) {
1486             UpdateRecord r = records.get(i);
1487             Receiver receiver = r.mReceiver;
1488 
1489             Location lastLoc = r.mLastFixBroadcast;
1490             if ((lastLoc == null) || shouldBroadcastSafe(location, lastLoc, r)) {
1491                 if (lastLoc == null) {
1492                     lastLoc = new Location(location);
1493                     r.mLastFixBroadcast = lastLoc;
1494                 } else {
1495                     lastLoc.set(location);
1496                 }
1497                 if (!receiver.callLocationChangedLocked(location)) {
1498                     Slog.w(TAG, "RemoteException calling onLocationChanged on " + receiver);
1499                     if (deadReceivers == null) {
1500                         deadReceivers = new ArrayList<Receiver>();
1501                     }
1502                     deadReceivers.add(receiver);
1503                 }
1504             }
1505 
1506             long prevStatusUpdateTime = r.mLastStatusBroadcast;
1507             if ((newStatusUpdateTime > prevStatusUpdateTime) &&
1508                 (prevStatusUpdateTime != 0 || status != LocationProvider.AVAILABLE)) {
1509 
1510                 r.mLastStatusBroadcast = newStatusUpdateTime;
1511                 if (!receiver.callStatusChangedLocked(provider, status, extras)) {
1512                     Slog.w(TAG, "RemoteException calling onStatusChanged on " + receiver);
1513                     if (deadReceivers == null) {
1514                         deadReceivers = new ArrayList<Receiver>();
1515                     }
1516                     if (!deadReceivers.contains(receiver)) {
1517                         deadReceivers.add(receiver);
1518                     }
1519                 }
1520             }
1521         }
1522         
1523         if (deadReceivers != null) {
1524             for (int i=deadReceivers.size()-1; i>=0; i--) {
1525                 removeUpdatesLocked(deadReceivers.get(i));
1526             }
1527         }
1528     }
1529 
1530     private class LocationWorkerHandler extends Handler {
1531 
1532         @Override
1533         public void handleMessage(Message msg) {
1534             try {
1535                 if (msg.what == MESSAGE_LOCATION_CHANGED) {
1536                     // log("LocationWorkerHandler: MESSAGE_LOCATION_CHANGED!");
1537 
1538                     synchronized (mLock) {
1539                         Location location = (Location) msg.obj;
1540                         String provider = location.getProvider();
1541                         boolean passive = (msg.arg1 == 1);
1542 
1543                         if (!passive) {
1544                             // notify other providers of the new location
1545                             for (int i = mProviders.size() - 1; i >= 0; i--) {
1546                                 LocationProviderInterface p = mProviders.get(i);
1547                                 if (!provider.equals(p.getName())) {
1548                                     p.updateLocation(location);
1549                                 }
1550                             }
1551                         }
1552 
1553                         if (isAllowedBySettingsLocked(provider)) {
1554                             handleLocationChangedLocked(location, passive);
1555                         }
1556                     }
1557                 }
1558             } catch (Exception e) {
1559                 // Log, don't crash!
1560                 Slog.e(TAG, "Exception in LocationWorkerHandler.handleMessage:", e);
1561             }
1562         }
1563     }
1564 
1565     private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
1566         @Override
1567         public void onReceive(Context context, Intent intent) {
1568             String action = intent.getAction();
1569             boolean queryRestart = action.equals(Intent.ACTION_QUERY_PACKAGE_RESTART);
1570             if (queryRestart
1571                     || action.equals(Intent.ACTION_PACKAGE_REMOVED)
1572                     || action.equals(Intent.ACTION_PACKAGE_RESTARTED)
1573                     || action.equals(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE)) {
1574                 synchronized (mLock) {
1575                     int uidList[] = null;
1576                     if (action.equals(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE)) {
1577                         uidList = intent.getIntArrayExtra(Intent.EXTRA_CHANGED_UID_LIST);
1578                     } else {
1579                         uidList = new int[]{intent.getIntExtra(Intent.EXTRA_UID, -1)};
1580                     }
1581                     if (uidList == null || uidList.length == 0) {
1582                         return;
1583                     }
1584                     for (int uid : uidList) {
1585                         if (uid >= 0) {
1586                             ArrayList<Receiver> removedRecs = null;
1587                             for (ArrayList<UpdateRecord> i : mRecordsByProvider.values()) {
1588                                 for (int j=i.size()-1; j>=0; j--) {
1589                                     UpdateRecord ur = i.get(j);
1590                                     if (ur.mReceiver.isPendingIntent() && ur.mUid == uid) {
1591                                         if (queryRestart) {
1592                                             setResultCode(Activity.RESULT_OK);
1593                                             return;
1594                                         }
1595                                         if (removedRecs == null) {
1596                                             removedRecs = new ArrayList<Receiver>();
1597                                         }
1598                                         if (!removedRecs.contains(ur.mReceiver)) {
1599                                             removedRecs.add(ur.mReceiver);
1600                                         }
1601                                     }
1602                                 }
1603                             }
1604                             ArrayList<ProximityAlert> removedAlerts = null;
1605                             for (ProximityAlert i : mProximityAlerts.values()) {
1606                                 if (i.mUid == uid) {
1607                                     if (queryRestart) {
1608                                         setResultCode(Activity.RESULT_OK);
1609                                         return;
1610                                     }
1611                                     if (removedAlerts == null) {
1612                                         removedAlerts = new ArrayList<ProximityAlert>();
1613                                     }
1614                                     if (!removedAlerts.contains(i)) {
1615                                         removedAlerts.add(i);
1616                                     }
1617                                 }
1618                             }
1619                             if (removedRecs != null) {
1620                                 for (int i=removedRecs.size()-1; i>=0; i--) {
1621                                     removeUpdatesLocked(removedRecs.get(i));
1622                                 }
1623                             }
1624                             if (removedAlerts != null) {
1625                                 for (int i=removedAlerts.size()-1; i>=0; i--) {
1626                                     removeProximityAlertLocked(removedAlerts.get(i).mIntent);
1627                                 }
1628                             }
1629                         }
1630                     }
1631                 }
1632             } else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
1633                 boolean noConnectivity =
1634                     intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
1635                 if (!noConnectivity) {
1636                     mNetworkState = LocationProvider.AVAILABLE;
1637                 } else {
1638                     mNetworkState = LocationProvider.TEMPORARILY_UNAVAILABLE;
1639                 }
1640                 NetworkInfo info =
1641                     (NetworkInfo)intent.getExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
1642 
1643                 // Notify location providers of current network state
1644                 synchronized (mLock) {
1645                     for (int i = mProviders.size() - 1; i >= 0; i--) {
1646                         LocationProviderInterface provider = mProviders.get(i);
1647                         if (provider.requiresNetwork()) {
1648                             provider.updateNetworkState(mNetworkState, info);
1649                         }
1650                     }
1651                 }
1652             }
1653         }
1654     };
1655 
1656     // Wake locks
1657 
1658     private void incrementPendingBroadcasts() {
1659         synchronized (mWakeLock) {
1660             if (mPendingBroadcasts++ == 0) {
1661                 try {
1662                     mWakeLock.acquire();
1663                     log("Acquired wakelock");
1664                 } catch (Exception e) {
1665                     // This is to catch a runtime exception thrown when we try to release an
1666                     // already released lock.
1667                     Slog.e(TAG, "exception in acquireWakeLock()", e);
1668                 }
1669             }
1670         }
1671     }
1672 
1673     private void decrementPendingBroadcasts() {
1674         synchronized (mWakeLock) {
1675             if (--mPendingBroadcasts == 0) {
1676                 try {
1677                     // Release wake lock
1678                     if (mWakeLock.isHeld()) {
1679                         mWakeLock.release();
1680                         log("Released wakelock");
1681                     } else {
1682                         log("Can't release wakelock again!");
1683                     }
1684                 } catch (Exception e) {
1685                     // This is to catch a runtime exception thrown when we try to release an
1686                     // already released lock.
1687                     Slog.e(TAG, "exception in releaseWakeLock()", e);
1688                 }
1689             }
1690         }
1691     }
1692 
1693     // Geocoder
1694 
1695     public String getFromLocation(double latitude, double longitude, int maxResults,
1696             GeocoderParams params, List<Address> addrs) {
1697         if (mGeocodeProvider != null) {
1698             return mGeocodeProvider.getFromLocation(latitude, longitude, maxResults,
1699                     params, addrs);
1700         }
1701         return null;
1702     }
1703 
1704 
1705     public String getFromLocationName(String locationName,
1706             double lowerLeftLatitude, double lowerLeftLongitude,
1707             double upperRightLatitude, double upperRightLongitude, int maxResults,
1708             GeocoderParams params, List<Address> addrs) {
1709 
1710         if (mGeocodeProvider != null) {
1711             return mGeocodeProvider.getFromLocationName(locationName, lowerLeftLatitude,
1712                     lowerLeftLongitude, upperRightLatitude, upperRightLongitude,
1713                     maxResults, params, addrs);
1714         }
1715         return null;
1716     }
1717 
1718     // Mock Providers
1719 
1720     private void checkMockPermissionsSafe() {
1721         boolean allowMocks = Settings.Secure.getInt(mContext.getContentResolver(),
1722                 Settings.Secure.ALLOW_MOCK_LOCATION, 0) == 1;
1723         if (!allowMocks) {
1724             throw new SecurityException("Requires ACCESS_MOCK_LOCATION secure setting");
1725         }
1726 
1727         if (mContext.checkCallingPermission(ACCESS_MOCK_LOCATION) !=
1728             PackageManager.PERMISSION_GRANTED) {
1729             throw new SecurityException("Requires ACCESS_MOCK_LOCATION permission");
1730         }            
1731     }
1732 
1733     public void addTestProvider(String name, boolean requiresNetwork, boolean requiresSatellite,
1734         boolean requiresCell, boolean hasMonetaryCost, boolean supportsAltitude,
1735         boolean supportsSpeed, boolean supportsBearing, int powerRequirement, int accuracy) {
1736         checkMockPermissionsSafe();
1737 
1738         if (LocationManager.PASSIVE_PROVIDER.equals(name)) {
1739             throw new IllegalArgumentException("Cannot mock the passive location provider");
1740         }
1741 
1742         long identity = Binder.clearCallingIdentity();
1743         synchronized (mLock) {
1744             MockProvider provider = new MockProvider(name, this,
1745                 requiresNetwork, requiresSatellite,
1746                 requiresCell, hasMonetaryCost, supportsAltitude,
1747                 supportsSpeed, supportsBearing, powerRequirement, accuracy);
1748             // remove the real provider if we are replacing GPS or network provider
1749             if (LocationManager.GPS_PROVIDER.equals(name)
1750                     || LocationManager.NETWORK_PROVIDER.equals(name)) {
1751                 LocationProviderInterface p = mProvidersByName.get(name);
1752                 if (p != null) {
1753                     p.enableLocationTracking(false);
1754                     removeProvider(p);
1755                 }
1756             }
1757             if (mProvidersByName.get(name) != null) {
1758                 throw new IllegalArgumentException("Provider \"" + name + "\" already exists");
1759             }
1760             addProvider(provider);
1761             mMockProviders.put(name, provider);
1762             mLastKnownLocation.put(name, null);
1763             updateProvidersLocked();
1764         }
1765         Binder.restoreCallingIdentity(identity);
1766     }
1767 
1768     public void removeTestProvider(String provider) {
1769         checkMockPermissionsSafe();
1770         synchronized (mLock) {
1771             MockProvider mockProvider = mMockProviders.get(provider);
1772             if (mockProvider == null) {
1773                 throw new IllegalArgumentException("Provider \"" + provider + "\" unknown");
1774             }
1775             long identity = Binder.clearCallingIdentity();
1776             removeProvider(mProvidersByName.get(provider));
1777             mMockProviders.remove(mockProvider);
1778             // reinstall real provider if we were mocking GPS or network provider
1779             if (LocationManager.GPS_PROVIDER.equals(provider) &&
1780                     mGpsLocationProvider != null) {
1781                 addProvider(mGpsLocationProvider);
1782             } else if (LocationManager.NETWORK_PROVIDER.equals(provider) &&
1783                     mNetworkLocationProvider != null) {
1784                 addProvider(mNetworkLocationProvider);
1785             }
1786             mLastKnownLocation.put(provider, null);
1787             updateProvidersLocked();
1788             Binder.restoreCallingIdentity(identity);
1789         }
1790     }
1791 
1792     public void setTestProviderLocation(String provider, Location loc) {
1793         checkMockPermissionsSafe();
1794         synchronized (mLock) {
1795             MockProvider mockProvider = mMockProviders.get(provider);
1796             if (mockProvider == null) {
1797                 throw new IllegalArgumentException("Provider \"" + provider + "\" unknown");
1798             }
1799             // clear calling identity so INSTALL_LOCATION_PROVIDER permission is not required
1800             long identity = Binder.clearCallingIdentity();
1801             mockProvider.setLocation(loc);
1802             Binder.restoreCallingIdentity(identity);
1803         }
1804     }
1805 
1806     public void clearTestProviderLocation(String provider) {
1807         checkMockPermissionsSafe();
1808         synchronized (mLock) {
1809             MockProvider mockProvider = mMockProviders.get(provider);
1810             if (mockProvider == null) {
1811                 throw new IllegalArgumentException("Provider \"" + provider + "\" unknown");
1812             }
1813             mockProvider.clearLocation();
1814         }
1815     }
1816 
1817     public void setTestProviderEnabled(String provider, boolean enabled) {
1818         checkMockPermissionsSafe();
1819         synchronized (mLock) {
1820             MockProvider mockProvider = mMockProviders.get(provider);
1821             if (mockProvider == null) {
1822                 throw new IllegalArgumentException("Provider \"" + provider + "\" unknown");
1823             }
1824             long identity = Binder.clearCallingIdentity();
1825             if (enabled) {
1826                 mockProvider.enable();
1827                 mEnabledProviders.add(provider);
1828                 mDisabledProviders.remove(provider);
1829             } else {
1830                 mockProvider.disable();
1831                 mEnabledProviders.remove(provider);
1832                 mDisabledProviders.add(provider);
1833             }
1834             updateProvidersLocked();
1835             Binder.restoreCallingIdentity(identity);
1836         }
1837     }
1838 
1839     public void clearTestProviderEnabled(String provider) {
1840         checkMockPermissionsSafe();
1841         synchronized (mLock) {
1842             MockProvider mockProvider = mMockProviders.get(provider);
1843             if (mockProvider == null) {
1844                 throw new IllegalArgumentException("Provider \"" + provider + "\" unknown");
1845             }
1846             long identity = Binder.clearCallingIdentity();
1847             mEnabledProviders.remove(provider);
1848             mDisabledProviders.remove(provider);
1849             updateProvidersLocked();
1850             Binder.restoreCallingIdentity(identity);
1851         }
1852     }
1853 
1854     public void setTestProviderStatus(String provider, int status, Bundle extras, long updateTime) {
1855         checkMockPermissionsSafe();
1856         synchronized (mLock) {
1857             MockProvider mockProvider = mMockProviders.get(provider);
1858             if (mockProvider == null) {
1859                 throw new IllegalArgumentException("Provider \"" + provider + "\" unknown");
1860             }
1861             mockProvider.setStatus(status, extras, updateTime);
1862         }
1863     }
1864 
1865     public void clearTestProviderStatus(String provider) {
1866         checkMockPermissionsSafe();
1867         synchronized (mLock) {
1868             MockProvider mockProvider = mMockProviders.get(provider);
1869             if (mockProvider == null) {
1870                 throw new IllegalArgumentException("Provider \"" + provider + "\" unknown");
1871             }
1872             mockProvider.clearStatus();
1873         }
1874     }
1875 
1876     private void log(String log) {
1877         if (Log.isLoggable(TAG, Log.VERBOSE)) {
1878             Slog.d(TAG, log);
1879         }
1880     }
1881     
1882     protected void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
1883         if (mContext.checkCallingOrSelfPermission(android.Manifest.permission.DUMP)
1884                 != PackageManager.PERMISSION_GRANTED) {
1885             pw.println("Permission Denial: can't dump LocationManagerService from from pid="
1886                     + Binder.getCallingPid()
1887                     + ", uid=" + Binder.getCallingUid());
1888             return;
1889         }
1890         
1891         synchronized (mLock) {
1892             pw.println("Current Location Manager state:");
1893             pw.println("  sProvidersLoaded=" + sProvidersLoaded);
1894             pw.println("  Listeners:");
1895             int N = mReceivers.size();
1896             for (int i=0; i<N; i++) {
1897                 pw.println("    " + mReceivers.get(i));
1898             }
1899             pw.println("  Location Listeners:");
1900             for (Receiver i : mReceivers.values()) {
1901                 pw.println("    " + i + ":");
1902                 for (Map.Entry<String,UpdateRecord> j : i.mUpdateRecords.entrySet()) {
1903                     pw.println("      " + j.getKey() + ":");
1904                     j.getValue().dump(pw, "        ");
1905                 }
1906             }
1907             pw.println("  Records by Provider:");
1908             for (Map.Entry<String, ArrayList<UpdateRecord>> i
1909                     : mRecordsByProvider.entrySet()) {
1910                 pw.println("    " + i.getKey() + ":");
1911                 for (UpdateRecord j : i.getValue()) {
1912                     pw.println("      " + j + ":");
1913                     j.dump(pw, "        ");
1914                 }
1915             }
1916             pw.println("  Last Known Locations:");
1917             for (Map.Entry<String, Location> i
1918                     : mLastKnownLocation.entrySet()) {
1919                 pw.println("    " + i.getKey() + ":");
1920                 i.getValue().dump(new PrintWriterPrinter(pw), "      ");
1921             }
1922             if (mProximityAlerts.size() > 0) {
1923                 pw.println("  Proximity Alerts:");
1924                 for (Map.Entry<PendingIntent, ProximityAlert> i
1925                         : mProximityAlerts.entrySet()) {
1926                     pw.println("    " + i.getKey() + ":");
1927                     i.getValue().dump(pw, "      ");
1928                 }
1929             }
1930             if (mProximitiesEntered.size() > 0) {
1931                 pw.println("  Proximities Entered:");
1932                 for (ProximityAlert i : mProximitiesEntered) {
1933                     pw.println("    " + i + ":");
1934                     i.dump(pw, "      ");
1935                 }
1936             }
1937             pw.println("  mProximityReceiver=" + mProximityReceiver);
1938             pw.println("  mProximityListener=" + mProximityListener);
1939             if (mEnabledProviders.size() > 0) {
1940                 pw.println("  Enabled Providers:");
1941                 for (String i : mEnabledProviders) {
1942                     pw.println("    " + i);
1943                 }
1944                 
1945             }
1946             if (mDisabledProviders.size() > 0) {
1947                 pw.println("  Disabled Providers:");
1948                 for (String i : mDisabledProviders) {
1949                     pw.println("    " + i);
1950                 }
1951                 
1952             }
1953             if (mMockProviders.size() > 0) {
1954                 pw.println("  Mock Providers:");
1955                 for (Map.Entry<String, MockProvider> i : mMockProviders.entrySet()) {
1956                     i.getValue().dump(pw, "      ");
1957                 }
1958             }
1959             for (LocationProviderInterface provider: mProviders) {
1960                 String state = provider.getInternalState();
1961                 if (state != null) {
1962                     pw.println(provider.getName() + " Internal State:");
1963                     pw.write(state);
1964                 }
1965             }
1966         }
1967     }
1968 }
Android framework classes and servicesRSSAtom
Main cache: 5 gets (0 hits + 5 misses); 0 sets, 5 failed sets.
Large cache: 1 gets (1 hits + 0 misses); 0 sets, 0 failed sets.