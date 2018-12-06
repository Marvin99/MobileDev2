package me.yablonskyi.eastside.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import me.chandansharma.wallhippo.R;
import me.yablonskyi.eastside.adapter.PictureListAdapter;
import me.yablonskyi.eastside.model.PictureDetail;
import me.yablonskyi.eastside.utils.ApiUrls;
import me.yablonskyi.eastside.utils.AppController;

/**
 * A simple {@link Fragment} subclass.
 */
public class PictureListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    //get the tag of the class
    public static final String TAG = PictureListFragment.class.getSimpleName();

    //ArrayList of the all picture details
    ArrayList<PictureDetail> mPictureDetailsList = new ArrayList<>();

    //Picture Url
    private String mPictureUrl;
    private PictureListAdapter mPictureListAdapter;
    private GridLayoutManager mGridLayoutManager;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mProgressBar;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_picture_list, container, false);

        mPictureUrl = ApiUrls.PICTURE_BASE_URL + ApiUrls.PHOTO_TAG + "?" + ApiUrls.API_KEY_TAG
                + "=" + ApiUrls.API_KEY + "&" + ApiUrls.PER_PAGE_ITEM_OFFSET + "=" + "30";
        Log.d(TAG, mPictureUrl);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        getPictureListData(mPictureUrl);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mPictureListAdapter = new PictureListAdapter(getActivity(), mPictureDetailsList);
        mGridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setAdapter(mPictureListAdapter);
        mSwipeRefreshLayout = (SwipeRefreshLayout)  rootView.findViewById(R.id.swipeContainer);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        return rootView;
    }


    private void getPictureListData(String pictureUrl) {
        mProgressBar.setVisibility(View.VISIBLE);
        //Tag to cancel the request
        String jsonArrayTag = "jsonArrayTag";
        JsonArrayRequest pictureJsonArray = new JsonArrayRequest(pictureUrl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        mProgressBar.setVisibility(View.GONE);
                        try {
                            Log.d(TAG, response.toString());

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject singlePictureDetailObject = (JSONObject) response.get(i);

                                PictureDetail singlePictureDetail = new PictureDetail(
                                        singlePictureDetailObject.getString("id"),
                                        singlePictureDetailObject.getInt("likes"),
                                        singlePictureDetailObject.getJSONObject("links").getString("download"),
                                        singlePictureDetailObject.getJSONObject("urls").getString("regular"),
                                        singlePictureDetailObject.getJSONObject("user").getString("name"),
                                        singlePictureDetailObject.getJSONObject("user").getString("username"),
                                        singlePictureDetailObject.getString("color"),
                                        singlePictureDetailObject.getJSONObject("user").
                                                getJSONObject("profile_image").getString("medium"));
                                mPictureDetailsList.add(singlePictureDetail);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mPictureListAdapter.notifyDataSetChanged();
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

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
