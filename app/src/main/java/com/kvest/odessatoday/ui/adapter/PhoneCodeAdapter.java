package com.kvest.odessatoday.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.kvest.odessatoday.datamodel.PhoneCodeMetadata;

/**
 * Created by kvest on 01.09.16.
 */
public class PhoneCodeAdapter extends BaseAdapter {
    private static final PhoneCodeMetadata[] DATA = new PhoneCodeMetadata[] {
//        new PhoneCodeMetadata(PhoneCodeMetadata.ID_UKRAINE,),
//        new PhoneCodeMetadata(PhoneCodeMetadata.ID_RUSSIA,),
//        new PhoneCodeMetadata(PhoneCodeMetadata.ID_BELARUS,),
//        new PhoneCodeMetadata(PhoneCodeMetadata.ID_ARMENIA,),
//        new PhoneCodeMetadata(PhoneCodeMetadata.ID_AZERBAIJAN,),
//        new PhoneCodeMetadata(PhoneCodeMetadata.ID_KAZAKHSTAN,),
//        new PhoneCodeMetadata(PhoneCodeMetadata.ID_KYRGYZSTAN,),
//        new PhoneCodeMetadata(PhoneCodeMetadata.ID_MOLDOVA,),
//        new PhoneCodeMetadata(PhoneCodeMetadata.ID_TAJIKISTAN,),
//        new PhoneCodeMetadata(PhoneCodeMetadata.ID_TURKMENISTAN,),
//        new PhoneCodeMetadata(PhoneCodeMetadata.ID_UZBEKISTAN,),
    };
//
//
//    Версия
//    1) Не добавляется фотка
//    2) Добавить код страны
//    3) Выводить сообщение после покупки билетов
//    Список ликов на с3 и на лыже
//    На белой(и на с3 и на лыже) девайсине неверно отображаются радиобатоны
//
//    Краши
//    Иероглифы в Ками + вообще все как то странно с отзывами
//
//
//    https://docs.google.com/spreadsheets/d/1UBq7v9Ipt0vuzBdxNsLhXdu7E8oxTpNirf3T5j2sLhA/edit#gid=0
//
//    Добавили комент с рейтингом(в онлайне) - нас вернуло на список коментов. Если еще наз нажать Добавить комент - то можно поставить рейтинг. Если вернуться к событию, и опять открыть коменты - то рейтинг поставить нельзя будет
//    fix bugs from bug-reports
//    Проверить что в запросе на список events - все события(не все в выставках)
//    В детали добавить Pull to refresh?
//    Проверить overdraw
//    проанализировать объем трафика
//    проверить на мемори лики! - что вложенные фрагменты померли
//    Проыерить что вся работа с БД не в UI потоке
//
//    content query --uri content://com.kvest.odessatoday/cinemas --where cinema_id=727
//
//    ukraine
//            russia
//    belarus
//            armenia
//    azerbaijan
//            kazakhstan
//    kyrgyzstan
//            moldova
//    tajikistan
//            turkmenistan
//    uzbekistan

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
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
