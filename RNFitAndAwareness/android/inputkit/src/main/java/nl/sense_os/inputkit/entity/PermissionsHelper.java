package nl.sense_os.inputkit.entity;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.INTERNET;
import static nl.sense_os.inputkit.constant.Permissions.STEP_COUNT;

/**
 * Created by panjiyudasetya on 6/19/17.
 */

public class PermissionsHelper {
    private PermissionsHelper() { }

    private static final String[] STEP_COUNT_PERMISSIONS = {
            INTERNET,
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION
    };

    /**
     * Helper function to detect all permission has been granted by user.
     * @param activity host activity
     * @param permissions required permissions
     * @return True if permissions granted, False otherwise.
     */
    public static boolean isAllPermissionsGranted(@NonNull Activity activity, @NonNull String[] permissions) {
        if (permissions == null || permissions.length == 0) return true;

        try {
            PackageInfo info = activity.getPackageManager()
                    .getPackageInfo(activity.getPackageName(), PackageManager.GET_PERMISSIONS);

            List<String> grantedPermissions = new ArrayList<>();
            if (info.requestedPermissions != null) {
                for (int i = 0; i < info.requestedPermissions.length; i++) {
                    if ((info.requestedPermissionsFlags[i] & PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0) {
                        grantedPermissions.add(info.requestedPermissions[i]);
                    }
                }
            }

            return grantedPermissions.containsAll(Arrays.asList(permissions));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Helper function to get all required permission for specific Input Kit permission
     * @param rnArrayPermissions React native permissions
     * @return Required Android permissions
     * @throws JSONException if something went wrong when iterating React native permissions
     */
    public static String[] getAllPermissions(@NonNull JSONArray rnArrayPermissions) throws JSONException {
        String[] rnPermissions = getRnPermissions(rnArrayPermissions);
        List<String> listPermissions = new ArrayList<>();
        for (String permission : rnPermissions) {
            if (permission.equals(STEP_COUNT)) {
                addStepCountPermissionToList(listPermissions);
            }
        }
        return listPermissions.toArray(new String[0]);
    }

    /**
     * Helper function to convert React Native json array into primitive String array
     * @param rnArrayPermissions React native permissions
     * @return Required Android permissions
     * @throws JSONException if something went wrong when iterating React native permissions
     */
    private static String[] getRnPermissions(@NonNull JSONArray rnArrayPermissions) throws JSONException {
        String[] availablePermission = new String[rnArrayPermissions.length()];
        for (int i = 0; i < rnArrayPermissions.length(); i++) {
            availablePermission[i] = rnArrayPermissions.getString(i);
        }
        return availablePermission;
    }

    /**
     * Helper function to store required Steps Count permission into list of permissions
     * @param listPermissions React native permissions
     * @return Required Android permissions
     * @throws JSONException if something went wrong when iterating React native permissions
     */
    private static void addStepCountPermissionToList(@NonNull List<String> listPermissions) {
        for (String permission : STEP_COUNT_PERMISSIONS) {
            if (!listPermissions.contains(permission)) listPermissions.add(permission);
        }
    }
}
