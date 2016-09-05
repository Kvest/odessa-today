package com.kvest.odessatoday.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kvest.odessatoday.R;
import com.kvest.odessatoday.datamodel.PhoneCodeMetadata;

/**
 * Created by kvest on 01.09.16.
 */
public class PhoneCodeAdapter extends BaseAdapter {
    private static final PhoneCodeMetadata[] DATA = new PhoneCodeMetadata[] {
        new PhoneCodeMetadata(PhoneCodeMetadata.ID_UKRAINE, R.string.ua, R.drawable.ic_flag_ua, "+380"),
        new PhoneCodeMetadata(PhoneCodeMetadata.ID_RUSSIA, R.string.ru, R.drawable.ic_flag_ru, "+7"),
        new PhoneCodeMetadata(PhoneCodeMetadata.ID_BELARUS, R.string.by, R.drawable.ic_flag_by, "+375"),
        new PhoneCodeMetadata(PhoneCodeMetadata.ID_ARMENIA, R.string.am, R.drawable.ic_flag_am, "+374"),
        new PhoneCodeMetadata(PhoneCodeMetadata.ID_AZERBAIJAN, R.string.az, R.drawable.ic_flag_az, "+994"),
        new PhoneCodeMetadata(PhoneCodeMetadata.ID_KAZAKHSTAN, R.string.kz, R.drawable.ic_flag_kz, "+7"),
        new PhoneCodeMetadata(PhoneCodeMetadata.ID_KYRGYZSTAN, R.string.kg, R.drawable.ic_flag_kg, "+996"),
        new PhoneCodeMetadata(PhoneCodeMetadata.ID_MOLDOVA, R.string.md, R.drawable.ic_flag_md, "+373"),
        new PhoneCodeMetadata(PhoneCodeMetadata.ID_TAJIKISTAN, R.string.tj, R.drawable.ic_flag_tj, "+992"),
        new PhoneCodeMetadata(PhoneCodeMetadata.ID_TURKMENISTAN, R.string.tm, R.drawable.ic_flag_tm, "+993"),
        new PhoneCodeMetadata(PhoneCodeMetadata.ID_UZBEKISTAN, R.string.uz, R.drawable.ic_flag_uz, "+998")
    };

    private LayoutInflater inflater;

    public PhoneCodeAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent, true);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent, false);
    }

    @Override
    public int getCount() {
        return DATA.length;
    }

    @Override
    public PhoneCodeMetadata getItem(int position) {
        return DATA[position];
    }

    @Override
    public long getItemId(int position) {
        return DATA[position].id;
    }

    public View getView(int position, View convertView, ViewGroup parent, boolean isForDropDown) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.phone_code_item, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }

        ViewHolder holder = (ViewHolder)convertView.getTag();
        holder.bind(DATA[position], isForDropDown);

        return convertView;
    }

    private static class ViewHolder {
        private ImageView flag;
        private TextView country;
        private TextView phoneCode;

        public ViewHolder(View view) {
            flag = (ImageView) view.findViewById(R.id.flag);
            country = (TextView)view.findViewById(R.id.country_name);
            phoneCode = (TextView)view.findViewById(R.id.phone_code);
        }

        public void bind(PhoneCodeMetadata data, boolean isForDropDown) {
            flag.setImageResource(data.flagRes);
            country.setText(data.countryNameRes);
            country.setVisibility(isForDropDown ? View.VISIBLE : View.GONE);
            phoneCode.setText(data.code);
        }
    }
}
