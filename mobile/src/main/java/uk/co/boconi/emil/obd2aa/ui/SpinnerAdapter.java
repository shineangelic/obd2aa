package uk.co.boconi.emil.obd2aa.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import uk.co.boconi.emil.obd2aa.R;
import uk.co.boconi.emil.obd2aa.model.ItemData;

public class SpinnerAdapter extends ArrayAdapter<ItemData> {

    private int groupid;
    private ArrayList<ItemData> list;
    private LayoutInflater inflater;

    public SpinnerAdapter(AppCompatActivity context, int groupid, int id, ArrayList<ItemData> list) {
        super(context, id, list);
        this.list = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.groupid = groupid;
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View itemView = inflater.inflate(groupid, parent, false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.img);
        imageView.setImageResource(list.get(position).getImageId());
        TextView textView = (TextView) itemView.findViewById(R.id.txt);
        textView.setText(list.get(position).getText());
        return itemView;
    }

    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }

}
