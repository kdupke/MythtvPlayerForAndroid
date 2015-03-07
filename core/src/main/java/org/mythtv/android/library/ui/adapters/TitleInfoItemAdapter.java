package org.mythtv.android.library.ui.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.mythtv.android.library.R;
import org.mythtv.android.library.core.domain.dvr.TitleInfo;

import java.util.List;

/**
 * Created by dmfrey on 11/29/14.
 */
public class TitleInfoItemAdapter extends RecyclerView.Adapter<TitleInfoItemAdapter.ViewHolder> {

    private List<TitleInfo> titleInfos;
    private TitleInfoItemClickListener titleInfoItemClickListener;

    public TitleInfoItemAdapter( List<TitleInfo> titleInfos, @NonNull TitleInfoItemClickListener titleInfoItemClickListener ) {

        this.titleInfos = titleInfos;
        this.titleInfoItemClickListener = titleInfoItemClickListener;

    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup viewGroup, int position ) {

        View v = LayoutInflater.from( viewGroup.getContext() ).inflate( R.layout.title_info_list_item, viewGroup, false );

        ViewHolder vh = new ViewHolder( v );
        return vh;
    }

    @Override
    public void onBindViewHolder( ViewHolder viewHolder, int position ) {

        final TitleInfo titleInfo = titleInfos.get( position );
        viewHolder.setTitle( titleInfo.getTitle() );
        viewHolder.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View v ) {

                titleInfoItemClickListener.titleInfoItemClicked( v, titleInfo );

            }

        });

    }

    @Override
    public int getItemCount() {

        return titleInfos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final View parent;
        private final TextView title;
        private String inetref;

        public ViewHolder( View v ) {
            super( v );

            this.parent = v;
            title = (TextView) parent.findViewById( R.id.title_info_item_title );

        }

        public void setTitle( CharSequence text ) {

            title.setText( text );

        }

        public void setInetref( String inetref ) {

            this.inetref = inetref;

        }

        public String getInetref() {
            return inetref;
        }

        public void setOnClickListener( View.OnClickListener listener ) {

            parent.setOnClickListener( listener );

        }

    }

    public interface TitleInfoItemClickListener {

        void titleInfoItemClicked( View v, TitleInfo titleInfo );

    }

}
