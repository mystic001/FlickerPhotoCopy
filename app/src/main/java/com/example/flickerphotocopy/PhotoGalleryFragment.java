package com.example.flickerphotocopy;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoGalleryFragment extends VisibleFragment {
    private static final String TAG ="PhotoGallery" ;

    private RecyclerView mPhotoRecyclerView;
    private List<GalleryItem> mItems = new ArrayList<>();


    public static PhotoGalleryFragment newInstance(){
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        PollService.setServiceAlarm(getActivity(), true);
        updateItem();

    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater){
        menuInflater.inflate(R.menu.searchvi, menu);

        MenuItem searchView = menu.findItem(R.id.menu_item_search);
        final SearchView searchVie = (SearchView) searchView.getActionView();

        searchVie.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                QueryPreferences.setOnStoredQuery(getActivity(), s);
                Log.d(TAG, "QueryTextSubmit: " + s);
                updateItem();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(TAG, "QueryTextChange: " + s);
                return false;
            }
        });
        searchVie.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = QueryPreferences.getStoredQuery(getActivity());
                searchVie.setQuery(query, false);
            }
        });

        MenuItem polling = menu.findItem(R.id.menu_item_toggle_polling);
        if (PollService.isServiceAlarm(getActivity())) {
           polling.setTitle(R.string.stop_polling);
        }else{
            polling.setTitle(R.string.start_polling);
        }

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_clear:
                QueryPreferences.setOnStoredQuery(getActivity(), null);
                updateItem();
                return true;

            case R.id.menu_item_toggle_polling:
                boolean shouldStartAlarm = !PollService.isServiceAlarm(getActivity());
                PollService.setServiceAlarm(getActivity(), shouldStartAlarm);
                getActivity().invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateItem(){
        String query = QueryPreferences.getStoredQuery(getActivity());
        new FetchItemsTask(query).execute();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view =inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        mPhotoRecyclerView = view.findViewById(R.id.photo_recycler);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        setupAdapter();
        return view;
    }

    private void setupAdapter() {
        if (isAdded()) {
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
        }
    }
    //this class below allows some sort of declarations all view you want to bind to your recyclerView must be declared here ;
    private class PhotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView mItemImageView;
        private TextView title, owner;
        private GalleryItem mgalleryItem;

// views declared are connected with there respective views in the xml code
        public PhotoHolder(@NonNull View itemView) {
            super(itemView);
            mItemImageView = itemView.findViewById(R.id.thumbnail);
            title = itemView.findViewById(R.id.title);
            owner = itemView.findViewById(R.id.count);

            itemView.setOnClickListener(this);

        }
        public void bindGalleryItem(GalleryItem galleryItem){
            //it gets images directly from the url this line of code saves you from writing a whole lot of codes
            Picasso.get().load(galleryItem.getUrl()).placeholder(R.drawable.action).into(mItemImageView);
            /* this line below is important cos it takes the GalleryItem variable (mgalleryItem)
             and sets it to the GalleryItem local variable (galleryItem) this makes it  */
            mgalleryItem = galleryItem;

        }

        @Override
        public void onClick(View view) {
            Log.i("Mgallery",mgalleryItem.getPageUrl().toString());
            Intent i = new Intent(PhotoPageActivity.newIntent(getActivity(),mgalleryItem.getPageUrl()));
            startActivity(i);
        }
    }

     class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
        private List<GalleryItem> mGalleryItems;


        public PhotoAdapter(List<GalleryItem> galleryItems) {

            mGalleryItems = galleryItems;
        }

        @NonNull
        @Override
        public PhotoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_item_gallery, parent, false);
            PhotoHolder holder = new PhotoHolder(view);
            return  holder;
        }


        @Override

        // this method is traditionally meant to  bind all your views to the recyclerView any view you want t
        public void onBindViewHolder(@NonNull PhotoHolder photoHolder, int position) {

            GalleryItem galleryItem = mGalleryItems.get(position);
            photoHolder.bindGalleryItem(galleryItem);
            photoHolder.title.setText(galleryItem.getOwner());
            photoHolder.owner.setText(galleryItem.getCaption());

        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }

    private class FetchItemsTask extends AsyncTask<Void,Void,List<GalleryItem>> {
        String mQuery;

        public FetchItemsTask(String Query){
            mQuery = Query;
        }


        @Override
        protected List<GalleryItem> doInBackground(Void... params) {

            if (mQuery == null) {
                 return new FlickrFetchr().fetchRecentPhotos();

            } else {

               return new FlickrFetchr().fetchSearchPhotos(mQuery);
            }

        }
        protected void onPostExecute(List<GalleryItem> items) {
            mItems = items;
            setupAdapter();
        }
    }

}
