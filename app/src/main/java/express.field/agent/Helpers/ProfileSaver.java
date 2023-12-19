package express.field.agent.Helpers;


import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import express.field.agent.Helpers.db.enums.DbObjectModel;
import express.field.agent.Model.Profile;
import express.field.agent.Request.exception.DbException;
import express.field.agent.Utils.Constants;
import express.field.agent.Utils.MapUtils;
import express.field.agent.Utils.ObjectUtils;

public class ProfileSaver {

    private static ProfileSaver mInstance;

    private Profile profile;

    private DbHelper mDbHelper;

    public static ProfileSaver getInstance() {
        if (mInstance == null) {
            synchronized (ProfileSaver.class) {
                mInstance = new ProfileSaver();
            }
        }

        return mInstance;
    }

    private ProfileSaver() {
        mDbHelper = DbHelper.getInstance();
    }

    /**
     * Method for saving user profile after successful login
     *
     * @param profileMap - Map structure which contains gathered user profile info
     * @throws DbException
     */
    public void saveProfile(Map<String, Object> profileMap) throws DbException {
        String id = null;

        if (MapUtils.getValue(Constants.LOGIN_RESPONSE_PERSON_ACTOR_ID, profileMap) instanceof String) {
            id = (String) MapUtils.getValue(Constants.LOGIN_RESPONSE_PERSON_ACTOR_ID, profileMap);
        } else if (MapUtils.getValue(Constants.LOGIN_RESPONSE_PERSON_ACTOR_ID, profileMap) instanceof Integer) {
            id = String.valueOf(MapUtils.getValue(Constants.LOGIN_RESPONSE_PERSON_ACTOR_ID, profileMap));
        }

        profileMap.put(Constants.DATABASE_ID, Constants.PROFILE_DB_ID);
        profileMap.put(Constants.MODELNAME, DbObjectModel.PROFILE);
        profileMap.put(Constants.ACTOR_ID, id);

        try {
            mDbHelper.save(ObjectUtils.changeObjectType(profileMap, HashMap.class));
        } catch (DbException e) {
            e.printStackTrace();
        }

        this.profile = ObjectUtils.mapToObject(profileMap, Profile.class);
    }

    /**
     * Method for saving user profile after successful login
     *
     * @param profile - Profile class object with user info
     * @throws DbException
     */
    public void saveProfile(Profile profile) throws DbException {
        HashMap<String, Object> profileMap = ObjectUtils.objectToMap(profile);

        String id = null;

        if (MapUtils.getValue(Constants.LOGIN_RESPONSE_PERSON_ACTOR_ID, profileMap) instanceof String) {
            id = (String) MapUtils.getValue(Constants.LOGIN_RESPONSE_PERSON_ACTOR_ID, profileMap);
        } else if (MapUtils.getValue(Constants.LOGIN_RESPONSE_PERSON_ACTOR_ID, profileMap) instanceof Integer) {
            id = String.valueOf(MapUtils.getValue(Constants.LOGIN_RESPONSE_PERSON_ACTOR_ID, profileMap));
        }

        profileMap.put(Constants.DATABASE_ID, Constants.PROFILE_DB_ID);
        profileMap.put(Constants.MODELNAME, DbObjectModel.PROFILE);
        profileMap.put(Constants.ACTOR_ID, id);

        try {
            mDbHelper.save(profileMap);
        } catch (DbException e) {
            e.printStackTrace();
        }

        this.profile = ObjectUtils.mapToObject(profileMap, Profile.class);
    }

    /**
     * Method for getting user profile information which is already saved on device after login
     *
     * @return - Profile class object
     */
    public Profile getProfile() {
        if (profile == null) {
            try {
                HashMap<String, Object> profileMap = mDbHelper.get(Constants.PROFILE_DB_ID);
                if (profileMap != null) {
                    profile = ObjectUtils.mapToObject(profileMap, Profile.class);
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        }

        return profile;
    }

    /**
     * Returns a boolean if user has certain permission.
     * The method could be used for deciding if form is editable or not regarding assigned role and permissions.
     *
     * @param permissionActionId - action id for required permission. Declare it in form as static variable or all separately in some set after acquire them.
     * @return - false if the login response doesn't contain such permission
     */
    public boolean userHasPermission(@NotNull String permissionActionId) {
//        for (ProfilePermission profilePermission : getProfile().getPermissions()) {
//            if (TextUtils.equals(permissionActionId, profilePermission.getActionId())) {
//                return true;
//            }
//        }

        return false;
    }
}
