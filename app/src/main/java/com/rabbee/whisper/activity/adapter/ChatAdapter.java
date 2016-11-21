package com.rabbee.whisper.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rabbee.whisper.R;
import com.rabbee.whisper.obj.DataProtocol;
import com.rabbee.whisper.obj.DataStream;
import com.rabbee.whisper.util.RuntimeInfo;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Rabbee on 2016/7/11.
 */
public class ChatAdapter extends ArrayAdapter<DataStream> {

    private int resourceId;

    public ChatAdapter(Context context, int textViewResourceId, List<DataStream> object) {
        super(context, textViewResourceId, object);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        DataStream dat = getItem(position);
        View view;
        ViewHolder viewHolder;

        if(convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.leftLayout = (LinearLayout) view.findViewById(R.id.left_layout_chat);
            viewHolder.rightLayout = (LinearLayout) view.findViewById(R.id.right_layout_chat);
            viewHolder.leftMsg = (TextView) view.findViewById(R.id.left_msg);
            viewHolder.rightMsg = (TextView) view.findViewById(R.id.right_msg);
            viewHolder.leftTime = (TextView) view.findViewById(R.id.left_time_chat);
            viewHolder.rightTime = (TextView) view.findViewById(R.id.right_time_chat);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        Date d = new Date(dat.getTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
//        String timeText = d.getYear() + "/" + d.getMonth() + "/" + d.getDay() + " " + d.getHours() + ":" + d.getMinutes() + ":" + d.getSeconds();
        String timeText = cal.get(Calendar.YEAR) + "/" + (cal.get(Calendar.MONTH)+1) + "/" + cal.get(Calendar.DATE) + " " + addZero(cal.get(Calendar.HOUR_OF_DAY)) + ":" + addZero(cal.get(Calendar.MINUTE)) + ":" + addZero(cal.get(Calendar.SECOND));
        if(dat.getType() == DataProtocol.TYPE_UI_RIGHT) {
            viewHolder.rightLayout.setVisibility(View.VISIBLE);
            viewHolder.leftLayout.setVisibility(View.GONE);
            viewHolder.rightMsg.setText((String)dat.getBody());
            viewHolder.rightTime.setText(timeText);
        }
        else{
            viewHolder.leftLayout.setVisibility(View.VISIBLE);
            viewHolder.rightLayout.setVisibility(View.GONE);
            viewHolder.leftMsg.setText((String)dat.getBody());
            viewHolder.leftTime.setText(timeText);
        }
//        //  时间戳显示与否
//        if(System.currentTimeMillis() - dat.getTimeMillis() > 1000 * 60 * 60 * 24)
//        {
//            viewHolder.leftTime.setVisibility(View.VISIBLE);
//            viewHolder.rightTime.setVisibility(View.VISIBLE);
//        }
//        else {
//            viewHolder.leftTime.setVisibility(View.GONE);
//            viewHolder.rightTime.setVisibility(View.GONE);
//        }
        return view;
    }

    class ViewHolder {
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView leftMsg;
        TextView rightMsg;
        TextView leftTime;
        TextView rightTime;
    }

    private String addZero(int x)
    {
        return  (x < 10 ? "0" + x : x).toString();
    }
}
