package com.teammoviealley.moviealleyapp.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
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
import com.teammoviealley.moviealleyapp.R;
import com.teammoviealley.moviealleyapp.adapter.MovieFavoriteAdapter;
import com.teammoviealley.moviealleyapp.adapter.MovieViewImageAdapter;
import com.teammoviealley.moviealleyapp.database.DatabaseHandler;
import com.teammoviealley.moviealleyapp.database.FavoriteMovies;
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

    DatabaseHandler db;
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

        db = new DatabaseHandler(getContext());

        setUpRecyclerView();

        return v;
    }

    void setUpRecyclerView(){
        Vector<FavoriteMovie> favMov = db.getFav();
        for(FavoriteMovie fav: favMov){
            Log.d("Movmov", fav.getTitle());
            if(email.equalsIgnoreCase(fav.getEmail()) == false)
                favMov.remove(fav);
        }
        RecyclerView favRecyclerView = v.findViewById(R.id.rv_favorite);
        MovieFavoriteAdapter adapter = new MovieFavoriteAdapter(v.getContext(), favMov);
        favRecyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(v.getContext(), LinearLayoutManager.VERTICAL, false);
        favRecyclerView.setLayoutManager(layoutManager);
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

        try {
            mCloudDBZone = mCloudDB.openCloudDBZone(mConfig, true);

        } catch (AGConnectCloudDBException e) {

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
                FavoriteMovie[] favArr = gson.fromJson(value.getFavoriteMovies(), FavoriteMovie[].class);
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





}