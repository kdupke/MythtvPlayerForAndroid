/*
 * MythTV Player An application for Android users to play MythTV Recordings and Videos
 * Copyright (c) 2015. Daniel Frey
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mythtv.android.player.app.loaders;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.mythtv.android.library.core.MainApplication;
import org.mythtv.android.library.core.domain.video.Video;
import org.mythtv.android.library.events.video.AllVideosEvent;
import org.mythtv.android.library.events.video.RequestAllVideosEvent;
import org.mythtv.android.library.events.video.VideoDetails;
import org.mythtv.android.library.persistence.domain.video.VideoConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dmfrey on 3/10/15.
 */
public class VideosAsyncTaskLoader extends AsyncTaskLoader<List<Video>> {

    private static final String TAG = VideosAsyncTaskLoader.class.getSimpleName();

    public enum Type { MOVIE, TELEVISION, HOMEVIDEO, ADULT, MUSICVIDEO }

    private VideosObserver mObserver;
    private List<Video> mVideos;

    private Type type;
    private String title;
    private Integer season;

    int limit, offset;

    public VideosAsyncTaskLoader( Context context, Type type, String title, Integer season, int limit, int offset ) {
        super( context );

        this.type = type;
        this.title = title;
        this.season = season;
        this.limit = limit;
        this.offset = offset;

    }

    @Override
    public List<Video> loadInBackground() {
        Log.v( TAG, "loadInBackground : type=" + type + ", title=" + title + ", season=" + season + ", limit=" + limit + ", offset=" + offset );
        List<Video> videos = new ArrayList<>();

        AllVideosEvent event = MainApplication.getInstance().getVideoService().requestAllVideos( new RequestAllVideosEvent( type.name(), title, season, limit, offset ) );
        if( event.isEntityFound() ) {

            for( VideoDetails details : event.getDetails() ) {

                 Video video = Video.fromDetails( details );
                 videos.add( video );

            }

        }

        return videos;
    }

    @Override
    public void deliverResult( List<Video> data ) {

        if( isReset() ) {

            // The Loader has been reset; ignore the result and invalidate the data.
            releaseResources( data );

            return;
        }

        // Hold a reference to the old data so it doesn't get garbage collected.
        // We must protect it until the new data has been delivered.
        List<Video> oldData = mVideos;
        mVideos = data;

        if( isStarted() ) {

            // If the Loader is in a started state, deliver the results to the
            // client. The superclass method does this for us.
            super.deliverResult( data );
        }

        // Invalidate the old data as we don't need it any more.
        if( oldData != null && oldData != data ) {

            releaseResources( oldData );

        }

    }

    @Override
    protected void onStartLoading() {

        if( null != mVideos ) {

            // Deliver any previously loaded data immediately.
            deliverResult( mVideos );

        }

        // Begin monitoring the underlying data source.
        if( null == mObserver ) {

            mObserver = new VideosObserver( mHandler, this );
            getContext().getContentResolver().registerContentObserver( VideoConstants.CONTENT_URI, true, mObserver );

        }

        if( takeContentChanged() || null == mVideos ) {

            // When the observer detects a change, it should call onContentChanged()
            // on the Loader, which will cause the next call to takeContentChanged()
            // to return true. If this is ever the case (or if the current data is
            // null), we force a new load.
            forceLoad();

        }

    }

    @Override
    protected void onStopLoading() {

        // The Loader is in a stopped state, so we should attempt to cancel the
        // current load (if there is one).
        cancelLoad();

        // Note that we leave the observer as is. Loaders in a stopped state
        // should still monitor the data source for changes so that the Loader
        // will know to force a new load if it is ever started again.

    }

    @Override
    protected void onReset() {

        // Ensure the loader has been stopped.
        onStopLoading();

        // At this point we can release the resources associated with 'mData'.
        if( null != mVideos ) {

            releaseResources( mVideos );
            mVideos = null;

        }

        // The Loader is being reset, so we should stop monitoring for changes.
        if( null != mObserver ) {

            getContext().getContentResolver().unregisterContentObserver( mObserver );
            mObserver = null;

        }

    }

    @Override
    public void onCanceled( List<Video> data ) {

        // Attempt to cancel the current asynchronous load.
        super.onCanceled( data );

        // The load has been canceled, so we should release the resources
        // associated with 'data'.
        releaseResources( data );

    }

    private void releaseResources( List<Video> data ) {

        // For a simple List, there is nothing to do. For something like a Cursor, we
        // would close it in this method. All resources associated with the Loader
        // should be released here.

    }

    private static final Handler mHandler = new Handler() {

        @Override
        public void handleMessage( Message msg ) {
            super.handleMessage( msg );

        }

    };

    private class VideosObserver extends ContentObserver {

        private VideosAsyncTaskLoader mLoader;

        public VideosObserver( Handler handler, VideosAsyncTaskLoader loader ) {
            super( handler );

            mLoader = loader;

        }

        @Override
        public boolean deliverSelfNotifications() {

            return super.deliverSelfNotifications();
        }

        @Override
        public void onChange( boolean selfChange ) {
            super.onChange( selfChange );

            mLoader.onContentChanged();

        }

        @Override
        public void onChange( boolean selfChange, Uri uri ) {
            super.onChange( selfChange, uri );

            mLoader.onContentChanged();

        }

    }

}
