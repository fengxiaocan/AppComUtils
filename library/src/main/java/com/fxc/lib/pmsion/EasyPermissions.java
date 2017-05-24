/*
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fxc.lib.pmsion;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility to request and check System permissions for apps targeting Android M (API >= 23).
 */
public class EasyPermissions {

    private static final String TAG        = "EasyPermissions";
    private static final String DIALOG_TAG = "RationaleDialogFragmentCompat";


    public interface PermissionCallbacks extends ActivityCompat.OnRequestPermissionsResultCallback {
        /**
         * 申请权限成功
         *
         * @param requestCode
         * @param perms
         */
        void onPermissionsSuccess(int requestCode, List<String> perms);

        /**
         * 申请权限失败
         *
         * @param requestCode
         * @param perms
         */
        void onPermissionsDafeat(int requestCode, List<String> perms);

    }

    /**
     * Check if the calling context has a set of permissions.
     *
     * @param context the calling context.
     * @param perms   one ore more permissions, such as {@code android.Manifest.permission.CAMERA}.
     *
     * @return true if all permissions are already granted, false if at least one permission is not yet granted.
     */
    public static boolean hasPermissions(@NonNull Context context, @NonNull String... perms) {
        // Always return true for SDK < M, let the system deal with the permissions
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        for (String perm : perms) {
            boolean hasPerm = (
                    ContextCompat.checkSelfPermission(context, perm) ==
                    PackageManager.PERMISSION_GRANTED);
            if (!hasPerm) {
                return false;
            }
        }

        return true;
    }

    /**
     * Request a set of permissions, showing rationale if the system requests it.
     *
     * @param object      Activity or Fragment requesting permissions. Should implement
     *                    {@link ActivityCompat.OnRequestPermissionsResultCallback}
     *                    or {@code android.support.v13.app.FragmentCompat.OnRequestPermissionsResultCallback}
     * @param rationale   a message explaining why the application needs this set of permissions, will be displayed if the user rejects the request the first
     *                    time.
     * @param requestCode request code to track this request, must be < 256.
     * @param perms       a set of permissions to be requested.
     */
    public static void requestPermissions(@NonNull final Object object, @NonNull String rationale, final int requestCode, @NonNull final String... perms) {
        requestPermissions(object, rationale, android.R.string.ok, android.R.string.cancel, requestCode, perms);
    }

    /**
     * Request a set of permissions, showing rationale if the system requests it.
     *
     * @param object         Activity or Fragment requesting permissions. Should implement
     *                       {@link ActivityCompat.OnRequestPermissionsResultCallback}
     *                       or {@code android.support.v13.app.FragmentCompat.OnRequestPermissionsResultCallback}
     * @param rationale      a message explaining why the application needs this set of permissions, will be displayed if the user rejects the request the first
     *                       time.
     * @param positiveButton custom text for positive button
     * @param negativeButton custom text for negative button
     * @param requestCode    request code to track this request, must be < 256.
     * @param perms          a set of permissions to be requested.
     */
    @SuppressLint("NewApi")
    public static void requestPermissions(@NonNull final Object object, @NonNull String rationale, @StringRes int positiveButton, @StringRes int negativeButton, final int requestCode, @NonNull final String... perms) {

        checkCallingObjectSuitability(object);

        boolean shouldShowRationale = false;
        for (String perm : perms) {
            shouldShowRationale = shouldShowRationale ||
                                  shouldShowRequestPermissionRationale(object, perm);
        }

        if (shouldShowRationale) {
            if (getSupportFragmentManager(object) != null) {
                showRationaleDialogFragmentCompat(getSupportFragmentManager(object), rationale, positiveButton, negativeButton, requestCode, perms);
            } else if (getFragmentManager(object) != null) {
                showRationaleDialogFragment(getFragmentManager(object), rationale, positiveButton, negativeButton, requestCode, perms);
            } else {
                showRationaleAlertDialog(object, rationale, positiveButton, negativeButton, requestCode, perms);
            }
        } else {
            executePermissionsRequest(object, perms, requestCode);
        }
    }

    /**
     * Show a {@link RationaleDialogFragmentCompat} explaining permission request rationale.
     */
    @RequiresApi(Build.VERSION_CODES.HONEYCOMB)
    private static void showRationaleDialogFragmentCompat(@NonNull final android.support.v4.app.FragmentManager fragmentManager, @NonNull String rationale, @StringRes int positiveButton, @StringRes int negativeButton, final int requestCode, @NonNull final String... perms) {

        RationaleDialogFragmentCompat fragment = RationaleDialogFragmentCompat.newInstance(positiveButton, negativeButton, rationale, requestCode, perms);
        fragment.show(fragmentManager, DIALOG_TAG);
    }

    /**
     * Show a {@link RationaleDialogFragment} explaining permission request rationale.
     */
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private static void showRationaleDialogFragment(@NonNull final android.app.FragmentManager fragmentManager, @NonNull String rationale, @StringRes int positiveButton, @StringRes int negativeButton, final int requestCode, @NonNull final String... perms) {

        RationaleDialogFragment fragment = RationaleDialogFragment.newInstance(positiveButton, negativeButton, rationale, requestCode, perms);
        fragment.show(fragmentManager, DIALOG_TAG);
    }

    /**
     * Show an {@link AlertDialog} explaining permission request rationale.
     */
    private static void showRationaleAlertDialog(@NonNull final Object object, @NonNull String rationale, @StringRes int positiveButton, @StringRes int negativeButton, final int requestCode, @NonNull final String... perms) {

        Activity activity = getActivity(object);
        if (activity == null) {
            throw new IllegalStateException("Can't show rationale dialog for null Activity");
        }

        new AlertDialog.Builder(activity).setCancelable(false).setMessage(rationale).setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                executePermissionsRequest(object, perms, requestCode);
            }
        }).setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // act as if the permissions were denied
                if (object instanceof PermissionCallbacks) {
                    ((PermissionCallbacks) object).onPermissionsDafeat(requestCode, Arrays.asList(perms));
                }
            }
        }).create().show();
    }

    /**
     * Check if at least one permission in the list of denied permissions has been permanently denied (user clicked "Never ask again").
     *
     * @param object            Activity or Fragment requesting permissions.
     * @param deniedPermissions list of denied permissions, usually from {@link PermissionCallbacks#onPermissionsDafeat(int, List)}
     *
     * @return {@code true} if at least one permission in the list was permanently denied.
     */
    public static boolean somePermissionPermanentlyDenied(@NonNull Object object, @NonNull List<String> deniedPermissions) {
        for (String deniedPermission : deniedPermissions) {
            if (permissionPermanentlyDenied(object, deniedPermission)) {
                return true;
            }
        }

        return false;
    }


    /**
     * Check if a permission has been permanently denied (user clicked "Never ask again").
     *
     * @param object           Activity or Fragment requesting permissions.
     * @param deniedPermission denied permission.
     *
     * @return {@code true} if the permissions has been permanently denied.
     */
    public static boolean permissionPermanentlyDenied(@NonNull Object object, @NonNull String deniedPermission) {
        return !shouldShowRequestPermissionRationale(object, deniedPermission);
    }


    /**
     * Handle the result of a permission request, should be called from the calling Activity's {@link ActivityCompat
     * .OnRequestPermissionsResultCallback#onRequestPermissionsResult(int, String[], int[])} method.
     * If any permissions were granted or denied, the {@code object} will receive the appropriate callbacks through {@link PermissionCallbacks} and
     * methods annotated with {@link AfterPermissionGranted} will be run if appropriate.
     *
     * @param requestCode  requestCode argument to permission result callback.
     * @param permissions  permissions argument to permission result callback.
     * @param grantResults grantResults argument to permission result callback.
     * @param receivers    an array of objects that have a method annotated with {@link AfterPermissionGranted} or implement {@link PermissionCallbacks}.
     */
    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, @NonNull Object... receivers) {

        // Make a collection of granted and denied permissions from the request.
        ArrayList<String> granted = new ArrayList<>();
        ArrayList<String> denied  = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            String perm = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                granted.add(perm);
            } else {
                denied.add(perm);
            }
        }

        // iterate through all receivers
        for (Object object : receivers) {
            // Report granted permissions, if any.
            if (!granted.isEmpty()) {
                if (object instanceof PermissionCallbacks) {
                    ((PermissionCallbacks) object).onPermissionsSuccess(requestCode, granted);
                }
            }

            // Report denied permissions, if any.
            if (!denied.isEmpty()) {
                if (object instanceof PermissionCallbacks) {
                    ((PermissionCallbacks) object).onPermissionsDafeat(requestCode, denied);
                }
            }

            // If 100% successful, call annotated methods
            if (!granted.isEmpty() && denied.isEmpty()) {
                runAnnotatedMethods(object, requestCode);
            }
        }

    }

    @TargetApi(23)
    private static boolean shouldShowRequestPermissionRationale(@NonNull Object object, @NonNull String perm) {
        if (object instanceof Activity) {
            return ActivityCompat.shouldShowRequestPermissionRationale((Activity) object, perm);
        } else if (object instanceof Fragment) {
            return ((Fragment) object).shouldShowRequestPermissionRationale(perm);
        } else if (object instanceof android.app.Fragment) {
            return ((android.app.Fragment) object).shouldShowRequestPermissionRationale(perm);
        } else {
            return false;
        }
    }

    @TargetApi(23)
    static void executePermissionsRequest(@NonNull Object object, @NonNull String[] perms, int requestCode) {
        checkCallingObjectSuitability(object);
        if (object instanceof Activity) {
            ActivityCompat.requestPermissions((Activity) object, perms, requestCode);
        } else if (object instanceof Fragment) {
            ((Fragment) object).requestPermissions(perms, requestCode);
        } else if (object instanceof android.app.Fragment) {
            ((android.app.Fragment) object).requestPermissions(perms, requestCode);
        }
    }

    @TargetApi(11)
    private static Activity getActivity(@NonNull Object object) {
        if (object instanceof Activity) {
            return ((Activity) object);
        } else if (object instanceof Fragment) {
            return ((Fragment) object).getActivity();
        } else if (object instanceof android.app.Fragment) {
            return ((android.app.Fragment) object).getActivity();
        } else {
            return null;
        }
    }

    @Nullable
    @SuppressLint("NewApi")
    private static android.support.v4.app.FragmentManager getSupportFragmentManager(@NonNull Object object) {

        if (object instanceof FragmentActivity) {
            // Support library FragmentActivity
            return ((FragmentActivity) object).getSupportFragmentManager();
        } else if (object instanceof Fragment) {
            // Support library Fragment
            return ((Fragment) object).getChildFragmentManager();
        }

        return null;
    }

    @Nullable
    private static android.app.FragmentManager getFragmentManager(@NonNull Object object) {
        if (object instanceof Activity) {
            // Framework Activity
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                // Above SDK 11, we can get Fragment manager
                return ((Activity) object).getFragmentManager();
            }
        } else if (object instanceof android.app.Fragment) {
            // Framework Fragment
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                // Above SDK 17, we can get a child Fragment manager
                return ((android.app.Fragment) object).getChildFragmentManager();
            } else {
                // Otherwise, we just return the standard Fragment manager
                return ((android.app.Fragment) object).getFragmentManager();
            }
        }

        return null;

    }

    private static void runAnnotatedMethods(@NonNull Object object, int requestCode) {
        Class clazz = object.getClass();
        if (isUsingAndroidAnnotations(object)) {
            clazz = clazz.getSuperclass();
        }
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(AfterPermissionGranted.class)) {
                // Check for annotated methods with matching request code.
                AfterPermissionGranted ann = method.getAnnotation(AfterPermissionGranted.class);
                if (ann.value() == requestCode) {
                    // Method must be void so that we can invoke it
                    if (method.getParameterTypes().length > 0) {
                        throw new RuntimeException(
                                "Cannot execute method " + method.getName() +
                                " because it is non-void method and/or has input parameters.");
                    }

                    try {
                        // Make method accessible if private
                        if (!method.isAccessible()) {
                            method.setAccessible(true);
                        }
                        method.invoke(object);
                    } catch (IllegalAccessException e) {
                        Log.e(TAG, "runDefaultMethod:IllegalAccessException", e);
                    } catch (InvocationTargetException e) {
                        Log.e(TAG, "runDefaultMethod:InvocationTargetException", e);
                    }
                }
            }
        }
    }

    private static void checkCallingObjectSuitability(@Nullable Object object) {
        if (object == null) {
            throw new NullPointerException("Activity or Fragment should not be null");
        }
        // Make sure Object is an Activity or Fragment
        boolean isActivity        = object instanceof Activity;
        boolean isSupportFragment = object instanceof Fragment;
        boolean isAppFragment     = object instanceof android.app.Fragment;
        boolean isMinSdkM = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
        if (!(isSupportFragment || isActivity ||
              (isAppFragment && isMinSdkM))) {
            if (isAppFragment) {
                throw new IllegalArgumentException("Target SDK needs to be greater than 23 if caller is android.app.Fragment");
            } else {
                throw new IllegalArgumentException("Caller must be an Activity or a Fragment.");
            }
        }
    }

    private static boolean isUsingAndroidAnnotations(@NonNull Object object) {
        if (!object.getClass().getSimpleName().endsWith("_")) {
            return false;
        }
        try {
            Class clazz = Class.forName("org.androidannotations.api.view.HasViews");
            return clazz.isInstance(object);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
