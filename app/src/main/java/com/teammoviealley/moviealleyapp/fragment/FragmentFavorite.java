package com.teammoviealley.moviealleyapp.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.huawei.agconnect.AGConnectInstance;
import com.huawei.agconnect.AGConnectOptionsBuilder;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.cloud.database.AGConnectCloudDB;
import com.huawei.agconnect.cloud.database.CloudDBZone;
import com.huawei.agconnect.cloud.database.CloudDBZoneConfig;
import com.huawei.agconnect.cloud.database.CloudDBZoneObjectList;
import com.huawei.agconnect.cloud.database.CloudDBZoneQuery;
import com.huawei.agconnect.cloud.database.CloudDBZoneSnapshot;
import com.huawei.agconnect.cloud.database.exceptions.AGConnectCloudDBException;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.service.AccountAuthService;
import com.teammoviealley.moviealleyapp.MainActivity;
import com.teammoviealley.moviealleyapp.R;
import com.teammoviealley.moviealleyapp.database.FavoriteMovies;
import com.teammoviealley.moviealleyapp.database.PopularMovies;
import com.teammoviealley.moviealleyapp.model.FavoriteMovie;
import com.teammoviealley.moviealleyapp.model.ObjectTypeInfoHelper;
import com.teammoviealley.moviealleyapp.model.StoreFavoriteMovie;

import java.util.Arrays;
import java.util.Vector;

public class FragmentFavorite extends Fragment {

    View v;
    Gson gson = new Gson();
    String email;
    TextView tvAccountName, tvEmail;
    Button btnLogOut;
    FavoriteMovies database = FavoriteMovies.getInstance();

    AGConnectCloudDB mCloudDB;
    CloudDBZone mCloudDBZone;
    CloudDBZoneConfig mConfig;

    public FragmentFavorite(String email) {
        // Required empty public constructor
        this.email = email;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_favorite, container, false);



        return v;
    }


    public void queryFromCloudDB(){
        mCloudDB.initialize(getContext());
        mCloudDB = AGConnectCloudDB.getInstance();
        try {
            mCloudDB.createObjectType(ObjectTypeInfoHelper.getObjectTypeInfo());

        } catch (AGConnectCloudDBException e) {

        }

        mConfig = new CloudDBZoneConfig("user",
                CloudDBZoneConfig.CloudDBZoneSyncProperty.CLOUDDBZONE_CLOUD_CACHE,
                CloudDBZoneConfig.CloudDBZoneAccessProperty.CLOUDDBZONE_PUBLIC);
        mConfig.setPersistenceEnabled(true);

        if (mCloudDBZone == null) {
            Toast.makeText(getActivity(), "CloudDBZone is null, try re-open it", Toast.LENGTH_SHORT).show();
            return;
        }

        Task<CloudDBZoneSnapshot<StoreFavoriteMovie>> queryTask = mCloudDBZone.executeQuery(CloudDBZoneQuery.where(StoreFavoriteMovie.class).equalTo("email",email),
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY);
        queryTask.addOnSuccessListener(new OnSuccessListener<CloudDBZoneSnapshot<StoreFavoriteMovie>>() {
            @Override
            public void onSuccess(CloudDBZoneSnapshot<StoreFavoriteMovie> snapshot) {
                processQueryResultCategory(snapshot);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    private void processQueryResultCategory (CloudDBZoneSnapshot<StoreFavoriteMovie> snapshot){
        CloudDBZoneObjectList<StoreFavoriteMovie> popInfoCursor = snapshot.getSnapshotObjects();

        try {
            while (popInfoCursor.hasNext()) {
                StoreFavoriteMovie value = popInfoCursor.next();
                FavoriteMovie[] favArr = gson.fromJson(value.getFavorite_movie(), FavoriteMovie[].class);
                Vector<FavoriteMovie> favlist = new Vector<FavoriteMovie>(Arrays.asList(favArr));
                database.setFavoriteMovies(favlist);

                //TAMPILIN recycler view
            }
        } catch (AGConnectCloudDBException e) {

            Toast.makeText(getActivity(), "onSnapshot:(getObject) " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        snapshot.release();
    }

    public void getFavoriteMovie() {

    }


    public void addFavorite(Integer id, String name, String path){
        FavoriteMovie fm = new FavoriteMovie(id, name, path);
        boolean success = database.checkAndAdd(fm);
        if(success){
            syncFavorite();
        }
        else{

        }
    }

    public void syncFavorite(){
        String json = gson.toJson(database.getFavoriteMovies());
        StoreFavoriteMovie store = new StoreFavoriteMovie(email,json);
        upsertToCloudDB(store);
    }

    public void upsertToCloudDB(StoreFavoriteMovie store){
        mCloudDB.initialize(getContext());
        mCloudDB = AGConnectCloudDB.getInstance();
        try {
            mCloudDB.createObjectType(ObjectTypeInfoHelper.getObjectTypeInfo());

        } catch (AGConnectCloudDBException e) {

        }

        mConfig = new CloudDBZoneConfig("user",
                CloudDBZoneConfig.CloudDBZoneSyncProperty.CLOUDDBZONE_CLOUD_CACHE,
                CloudDBZoneConfig.CloudDBZoneAccessProperty.CLOUDDBZONE_PUBLIC);
        mConfig.setPersistenceEnabled(true);

        if (mCloudDBZone == null) {
            Toast.makeText(getActivity(), "CloudDBZone is null, try re-open it", Toast.LENGTH_SHORT).show();
            return;
        }
        Task<Integer> upsertTask = mCloudDBZone.executeUpsert(store);
        upsertTask.addOnSuccessListener(new OnSuccessListener<Integer>() {
            @Override
            public void onSuccess(Integer cloudDBZoneResult) {
                Log.i("TAG", "Upsert " + cloudDBZoneResult + " records");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {

            }
        });
    }


}