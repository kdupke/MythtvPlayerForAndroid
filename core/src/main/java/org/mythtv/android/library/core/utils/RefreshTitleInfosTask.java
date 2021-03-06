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

package org.mythtv.android.library.core.utils;

import android.os.AsyncTask;
import android.util.Log;

import org.mythtv.android.library.core.MainApplication;
import org.mythtv.android.library.events.dvr.TitleInfosUpdatedEvent;
import org.mythtv.android.library.events.dvr.UpdateTitleInfosEvent;

/**
 * Created by dmfrey on 3/14/15.
 */
public class RefreshTitleInfosTask extends AsyncTask<Void, Void, TitleInfosUpdatedEvent> {

    private final String TAG = RefreshTitleInfosTask.class.getSimpleName();

    public interface OnRefreshRecordedProgramTaskListener {

        public void onRefreshComplete( boolean updated );

    }

    OnRefreshRecordedProgramTaskListener mListener;

    public RefreshTitleInfosTask( OnRefreshRecordedProgramTaskListener listener ) {

        if( null != listener ) {

            mListener = listener;

        }

    }

    @Override
    protected TitleInfosUpdatedEvent doInBackground( Void... params ) {
        Log.v(TAG, "doInBackground : enter");

        if( MainApplication.getInstance().isConnected() ) {

            TitleInfosUpdatedEvent updated = MainApplication.getInstance().getDvrApiService().updateTitleInfos( new UpdateTitleInfosEvent() );

            return updated;
        }

        Log.v( TAG, "doInBackground : exit" );
        return TitleInfosUpdatedEvent.notUpdated();
    }

    @Override
    protected void onPostExecute( TitleInfosUpdatedEvent event ) {

        if( null != mListener ) {

            mListener.onRefreshComplete( event.isEntityFound() );

        }

        super.onPostExecute( event );

    }

}
