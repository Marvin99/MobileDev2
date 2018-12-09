package me.yablonskyi.eastside.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

import me.yablonskyi.eastside.R;
import me.yablonskyi.eastside.adapter.CollectionListAdapter;
import me.yablonskyi.eastside.model.CollectionDetail;
import me.yablonskyi.eastside.utils.ApiUrls;
import me.yablonskyi.eastside.utils.AppController;


/**
 * Created by Marvin
 * A simple {@link Fragment} subclass.
 */
public class CollectionPictureListFragment extends Fragment {

    //get the tag of the class
    public static final String TAG = CollectionPictureListFragment.class.getSimpleName();

    //ArrayList of the all picture details
    ArrayList<CollectionDetail> mCollectionDetailList = new ArrayList<>();

    //Picture Url
    private String mCollectionUrl;
    private CollectionListAdapter mCollectionListAdapter;
    private GridLayoutManager mGridLayoutManager;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;

    //Counter Variable that count the visible item as well as the pageCounter
    private int visibleItemCounter;
    private int totalItemCounter;
    private int firstVisibleItem;
    private int pageCounter = 1;
    private int previousTotalItem = 0;
    private int visibleThresholdItem = 4;
    private boolean processingNewItem = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_collection_picture_list, container, false);
        mCollectionUrl = ApiUrls.PICTURE_BASE_URL + ApiUrls.COLLECTION_FEATURED_TAG + "?" + ApiUrls.API_KEY_TAG
                + "=" + ApiUrls.API_KEY + "&" + ApiUrls.PER_PAGE_ITEM_OFFSET + "=" + "30";
        Log.d(TAG, mCollectionUrl);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        getCollectionListData(mCollectionUrl);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mCollectionListAdapter = new CollectionListAdapter(getActivity(), mCollectionDetailList);
        mGridLayoutManager = new GridLayoutManager(getActivity(), 1);

        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setAdapter(mCollectionListAdapter);

        return rootView;
    }

    private void getCollectionListData(String collectionUrl) {
        mProgressBar.setVisibility(View.VISIBLE);
        //Tag to cancel the request
        String jsonArrayTag = "jsonArrayTag";

        JsonArrayRequest pictureJsonArray = new JsonArrayRequest(collectionUrl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        mProgressBar.setVisibility(View.GONE);
                        mProgressBar.setVisibility(View.GONE);
                        try {
                            Log.d(TAG, response.toString());

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject singleCollectionDetailObject = (JSONObject) response.get(i);

                                CollectionDetail singleCollectionDetail = new CollectionDetail(
                                        singleCollectionDetailObject.getString("id"),
                                        singleCollectionDetailObject.getString("title"),
                                        singleCollectionDetailObject.getJSONObject("user")
                                                .getJSONObject("profile_image").getString("medium"),
                                        singleCollectionDetailObject.getJSONObject("cover_photo").
                                                getJSONObject("urls").getString("regular"),
                                        singleCollectionDetailObject.getJSONObject("user")
                                                .getString("name"));
                                mCollectionDetailList.add(singleCollectionDetail);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mCollectionListAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, error.getMessage());
                    }
                });
        //Adding request to the queue
        AppController.getInstance().addToRequestQueue(pictureJsonArray, jsonArrayTag);
    }
}
