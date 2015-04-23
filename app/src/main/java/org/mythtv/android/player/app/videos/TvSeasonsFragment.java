package org.mythtv.android.player.app.videos;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.mythtv.android.R;
import org.mythtv.android.library.core.domain.video.Video;
import org.mythtv.android.player.app.AbstractBaseFragment;
import org.mythtv.android.player.app.loaders.VideosAsyncTaskLoader;
import org.mythtv.android.player.common.ui.adapters.VideoTvItemAdapter;

import java.util.Collections;
import java.util.List;

/**
 * Created by dmfrey on 12/3/14.
 */
public class TvSeasonsFragment extends AbstractBaseFragment implements LoaderManager.LoaderCallbacks<List<Video>>, VideoTvItemAdapter.VideoItemClickListener {

    private static final String TAG = TvSeasonsFragment.class.getSimpleName();

    RecyclerView mRecyclerView;
    VideoTvItemAdapter mAdapter;
    GridLayoutManager mLayoutManager;
    TextView mEmpty;
    Video mShow;

    @Override
    public Loader<List<Video>> onCreateLoader( int id, Bundle args ) {
        Log.v( TAG, "onCreateLoader : enter" );

        Log.v(TAG, "onCreateLoader : exit");
        return new VideosAsyncTaskLoader( getActivity(), VideosAsyncTaskLoader.Type.TELEVISION, mShow.getTitle(), mShow.getSeason() );
    }

    @Override
    public void onLoadFinished( Loader<List<Video>> loader, List<Video> videos ) {
        Log.v(TAG, "onLoadFinished : enter");

        if( !videos.isEmpty() ) {
            Log.v( TAG, "onLoadFinished : loaded videos from db" );

            Collections.sort( videos );

            mAdapter = new VideoTvItemAdapter( videos, this );
            mRecyclerView.setAdapter( mAdapter );

            mRecyclerView.setVisibility( View.VISIBLE );
            mEmpty.setVisibility( View.GONE );

        } else {

            mRecyclerView.setVisibility( View.GONE );
            mEmpty.setVisibility( View.VISIBLE );

        }

        Log.v(TAG, "onLoadFinished : exit");
    }

    @Override
    public void onLoaderReset( Loader<List<Video>> loader ) {

        mRecyclerView.setAdapter( null );

    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {

        View view = inflater.inflate( R.layout.video_tv_list, container, false );

        mRecyclerView = (RecyclerView) view.findViewById( R.id.list );
        mLayoutManager = new GridLayoutManager( getActivity(), 2 );
        mRecyclerView.setLayoutManager(mLayoutManager );
        mEmpty = (TextView) view.findViewById( R.id.empty );

        return view;
    }

    public void setShow( Video show ) {
        Log.v( TAG, "reload : enter" );

        mShow = show;
        getLoaderManager().initLoader(0, null, this );

        Log.v(TAG, "videos : exit");
    }

    public void videoItemClicked( View v, Video video ) {

        Bundle args = new Bundle();
        args.putSerializable( VideoDetailsFragment.VIDEO_KEY, video );

        Intent videoDetails = new Intent( getActivity(), VideoDetailsActivity.class );
        videoDetails.putExtras( args );

        if( Build.VERSION.SDK_INT >= 16 ) {

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation( getActivity(), null );
            getActivity().startActivity(videoDetails, options.toBundle());

        } else {

            startActivity( videoDetails );

        }

    }

    @Override
    public void connected() {

//        mRecyclerView.setVisibility( View.VISIBLE );
//        mEmpty.setVisibility(View.GONE);

    }

    @Override
    public void notConnected() {

//        mRecyclerView.setVisibility( View.GONE );
//        mEmpty.setVisibility(View.VISIBLE);

    }

}