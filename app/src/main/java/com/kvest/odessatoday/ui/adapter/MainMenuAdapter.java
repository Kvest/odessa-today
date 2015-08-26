package com.kvest.odessatoday.ui.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.ui.fragment.MainMenuFragment;

/**
 * Created by Kvest on 02.02.2015.
 */
public class MainMenuAdapter extends BaseAdapter {
    private static final int EMPTY_SELECTED_POSITION = -1;
    private static final int VIEW_TYPE_COUNT = 3;
    private static final MainMenuItem[] items = new MainMenuItem[]{
//            new CategoryItem(R.string.menu_category_event),
//            new DividerItem(),
//            new SubcategoryItem(MainMenuFragment.MENU_FILMS_ID, R.string.menu_films, R.drawable.ic_menu_films, R.drawable.ic_menu_films_selected, true),
//            new SubcategoryItem(MainMenuFragment.MENU_CONCERT_ID, R.string.menu_concert, R.drawable.ic_menu_concert, R.drawable.ic_menu_concert_selected, false),
//            new SubcategoryItem(MainMenuFragment.MENU_PARTY_ID, R.string.menu_party, R.drawable.ic_menu_party, R.drawable.ic_menu_party_selected, false),
//            new SubcategoryItem(MainMenuFragment.MENU_SPECTACLE_ID, R.string.menu_spectacle, R.drawable.ic_menu_spectacle, R.drawable.ic_menu_spectacle_selected, false),
//            new SubcategoryItem(MainMenuFragment.MENU_EXHIBITION_ID, R.string.menu_exhibition, R.drawable.ic_menu_exhibition, R.drawable.ic_menu_exhibition_selected, false),
//            new SubcategoryItem(MainMenuFragment.MENU_SPORT_ID, R.string.menu_sport, R.drawable.ic_menu_sport, R.drawable.ic_menu_sport_selected, false),
//            new SubcategoryItem(MainMenuFragment.MENU_WORKSHOP_ID, R.string.menu_workshop, R.drawable.ic_menu_workshop, R.drawable.ic_menu_workshop_selected, false),
//            new DividerItem(),
//            new CategoryItem(R.string.menu_category_place),
//            new DividerItem(),
//            new SubcategoryItem(MainMenuFragment.MENU_CINEMA_ID, R.string.menu_cinema, R.drawable.ic_menu_cinema, R.drawable.ic_menu_cinema_selected, true),
//            new SubcategoryItem(MainMenuFragment.MENU_THEATRE_ID, R.string.menu_theatre, R.drawable.ic_menu_theatre, R.drawable.ic_menu_theatre_selected, false),
//            new SubcategoryItem(MainMenuFragment.MENU_CONCERT_HALL_ID, R.string.menu_concert_hall, R.drawable.ic_menu_concert_hall, R.drawable.ic_menu_concert_hall_selected, false),
//            new SubcategoryItem(MainMenuFragment.MENU_CLUB_ID, R.string.menu_club, R.drawable.ic_menu_club, R.drawable.ic_menu_club_selected, false),
//            new SubcategoryItem(MainMenuFragment.MENU_MUSEUM_ID, R.string.menu_museum, R.drawable.ic_menu_museum, R.drawable.ic_menu_museum_selected, false),
//            new SubcategoryItem(MainMenuFragment.MENU_GALLERY_ID, R.string.menu_gallery, R.drawable.ic_menu_gallery, R.drawable.ic_menu_gallery_selected, false),
//            new SubcategoryItem(MainMenuFragment.MENU_ZOO_ID, R.string.menu_zoo, R.drawable.ic_menu_zoo, R.drawable.ic_menu_zoo_selected, false),
//            new SubcategoryItem(MainMenuFragment.MENU_QUEST_ID, R.string.menu_quest, R.drawable.ic_menu_quest, R.drawable.ic_menu_quest_selected, false),
//            new SubcategoryItem(MainMenuFragment.MENU_RESTAURANT_ID, R.string.menu_restaurant, R.drawable.ic_menu_restaurant, R.drawable.ic_menu_restaurant_selected, false),
//            new SubcategoryItem(MainMenuFragment.MENU_CAFE_ID, R.string.menu_cafe, R.drawable.ic_menu_cafe, R.drawable.ic_menu_cafe_selected, false),
//            new SubcategoryItem(MainMenuFragment.MENU_PIZZA_ID, R.string.menu_pizza, R.drawable.ic_menu_pizza, R.drawable.ic_menu_pizza_selected, false),
//            new SubcategoryItem(MainMenuFragment.MENU_SUSHI_ID, R.string.menu_sushi, R.drawable.ic_menu_sushi, R.drawable.ic_menu_sushi_selected, false),
//            new SubcategoryItem(MainMenuFragment.MENU_KARAOKE_ID, R.string.menu_karaoke, R.drawable.ic_menu_karaoke, R.drawable.ic_menu_karaoke_selected, false),
//            new SubcategoryItem(MainMenuFragment.MENU_SKATING_RINK_ID, R.string.menu_skating_rink, R.drawable.ic_menu_skating_rink, R.drawable.ic_menu_skating_rink_selected, false),
//            new SubcategoryItem(MainMenuFragment.MENU_BOWLING_ID, R.string.menu_bowling, R.drawable.ic_menu_bowling, R.drawable.ic_menu_bowling_selected, false),
//            new SubcategoryItem(MainMenuFragment.MENU_BILLIARD_ID, R.string.menu_billiard, R.drawable.ic_menu_billiard, R.drawable.ic_menu_billiard_selected, false),
//            new SubcategoryItem(MainMenuFragment.MENU_SAUNA_ID, R.string.menu_sauna, R.drawable.ic_menu_sauna, R.drawable.ic_menu_sauna_selected, false),
//            new SubcategoryItem(MainMenuFragment.MENU_BATH_ID, R.string.menu_bath, R.drawable.ic_menu_bath, R.drawable.ic_menu_bath_selected, false)
    };

    private int selectedItemPosition;
    private Context context;
    private int itemPadding, itemPaddingRight, itemTextSize, itemIconPadding;
    private int disabledItemBg, selectedItemBg, itemBg, itemTextColor, selectedItemTextColor;
    private int categoryBg, categoryTextColor;
    private int categoryVerticalPadding, categoryHorizontalPadding, categoryTextSiz;

    public MainMenuAdapter(Context context) {
        super();

        this.context = context;

        //init other fields
        selectedItemPosition = EMPTY_SELECTED_POSITION;
        categoryVerticalPadding = context.getResources().getDimensionPixelSize(R.dimen.main_menu_category_vertical_padding);
        categoryHorizontalPadding = context.getResources().getDimensionPixelSize(R.dimen.main_menu_category_horizontal_padding);
        categoryTextSiz = context.getResources().getDimensionPixelSize(R.dimen.main_menu_category_text_size);
        categoryBg = context.getResources().getColor(R.color.main_menu_category_bg);
        categoryTextColor = context.getResources().getColor(R.color.main_menu_category_text_color);
        itemPadding = context.getResources().getDimensionPixelSize(R.dimen.main_menu_item_padding);
        itemPaddingRight = context.getResources().getDimensionPixelSize(R.dimen.main_menu_item_padding_right);
        itemTextSize = context.getResources().getDimensionPixelSize(R.dimen.main_menu_item_text_size);
        itemIconPadding = context.getResources().getDimensionPixelSize(R.dimen.main_menu_item_icon_padding);
        disabledItemBg = context.getResources().getColor(R.color.main_menu_disabled_item_bg);
        selectedItemBg = context.getResources().getColor(R.color.main_menu_selected_item_bg);
        itemBg = context.getResources().getColor(R.color.main_menu_item_bg);
        itemTextColor = context.getResources().getColor(R.color.main_menu_item_text_color);
        selectedItemTextColor = context.getResources().getColor(R.color.main_menu_selected_item_text_color);
    }

    public int getSelectedItemPosition() {
        return selectedItemPosition;
    }

    public void setSelectedItemPosition(int selectedItemPosition) {
        this.selectedItemPosition = selectedItemPosition;

        notifyDataSetChanged();
    }

    public void setSelectedItemById(int selectedItemId) {
        for (int i = 0; i < items.length; ++i) {
            if (items[i].id == selectedItemId) {
                setSelectedItemPosition(i);

                break;
            }
        }
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public MainMenuItem getItem(int position) {
        return items[position];
    }

    @Override
    public long getItemId(int position) {
        return items[position].id;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        int viewType = getItemViewType(position);

        //create item view if needed
        if (view == null) {
            view = createView(viewType, parent);
        }

        //get item model
        MainMenuItem item = getItem(position);

        //bind data to the view
        switch (viewType) {
            case MainMenuItem.TYPE_CATEGORY :
                bindCategoryView((TextView) view, (CategoryItem) item);
                break;
            case MainMenuItem.TYPE_SUBCATEGORY :
                //common properties
//                view.setPadding(itemPadding, itemPadding, itemPaddingRight, itemPadding);
//                ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_PX, itemTextSize);
//                ((TextView) view).setCompoundDrawablePadding(itemIconPadding);
//
//                //specific properties
//                if (item.enable) {
//                    if (position == selectedItemPosition) {
//                        bindSelectedItem((TextView) view, (SubcategoryItem) item);
//                    } else {
//                        bindItem((TextView) view, (SubcategoryItem) item);
//                    }
//                } else {
//                    bindDisabledItem((TextView) view, (SubcategoryItem) item);
//                }
                break;
        }

        return view;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        return items[position].type;
    }

    private View createView(int viewType, ViewGroup parent) {
        switch (viewType) {
            case MainMenuItem.TYPE_DIVIDER :
                return LayoutInflater.from(context).inflate(R.layout.main_menu_item_divider, parent, false);
            case MainMenuItem.TYPE_CATEGORY :
                return LayoutInflater.from(context).inflate(R.layout.main_menu_item_category, parent, false);
            case MainMenuItem.TYPE_SUBCATEGORY :
                View view  = LayoutInflater.from(context).inflate(R.layout.main_menu_item_subcategory, parent, false);
                view.setTag(new SubcategoryViewHolder(view));
                return view;
        }

        return null;
    }

    private void bindCategoryView(TextView view, CategoryItem item) {
        view.setText(item.textRes);
//        view.setBackgroundColor(categoryBg);
//        view.setPadding(categoryHorizontalPadding, categoryVerticalPadding, categoryHorizontalPadding, categoryVerticalPadding);
//        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, categoryTextSiz);
//        view.setTextColor(categoryTextColor);
//        view.setCompoundDrawablePadding(0);
//        view.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }

    private void bindDisabledItem(TextView view, SubcategoryItem item) {
        view.setBackgroundColor(disabledItemBg);
        view.setTextColor(itemTextColor);
        view.setText(item.textRes);
        view.setCompoundDrawablesWithIntrinsicBounds(item.iconRes, 0, 0, 0);
    }

    private void bindItem(TextView view, SubcategoryItem item) {
        view.setBackgroundColor(itemBg);
        view.setTextColor(itemTextColor);
        view.setText(item.textRes);
        view.setCompoundDrawablesWithIntrinsicBounds(item.iconRes, 0, 0, 0);
    }

    private void bindSelectedItem(TextView view, SubcategoryItem item) {
        view.setBackgroundColor(selectedItemBg);
        view.setTextColor(selectedItemTextColor);
        view.setText(item.textRes);
        view.setCompoundDrawablesWithIntrinsicBounds(item.iconSelectedRes, 0, 0, 0);
    }

    public static class MainMenuItem {
        protected static final int TYPE_CATEGORY = 0;
        protected static final int TYPE_SUBCATEGORY = 1;
        protected static final int TYPE_DIVIDER = 2;

        public final int id;
        private final int type;
        public final boolean enable;

        public MainMenuItem(int id, int type, boolean enable) {
            this.id = id;
            this.type = type;
            this.enable = enable;
        }
    }

    public static class DividerItem extends MainMenuItem {
        public DividerItem() {
            super(-1, TYPE_DIVIDER, false);
        }
    }

    public static class CategoryItem extends MainMenuItem {
        private int textRes;

        public CategoryItem(int textRes) {
            super(-1, TYPE_CATEGORY, false);

            this.textRes = textRes;
        }
    }

    public static class SubcategoryItem extends MainMenuItem {
        private int textRes;
        private int iconRes;
        private int iconSelectedRes;

        public SubcategoryItem(int id, int textRes, int iconRes, int iconSelectedRes, boolean enable) {
            super(id, TYPE_SUBCATEGORY, enable);
            this.textRes = textRes;
            this.iconRes = iconRes;
            this.iconSelectedRes = iconSelectedRes;
        }
    }

    public static class SubcategoryViewHolder {
        public SubcategoryViewHolder(View view) {
            //TODO
        }
    }
}
