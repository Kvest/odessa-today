package com.kvest.odessatoday.ui.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.ui.fragment.MainMenuFragment;
import com.kvest.odessatoday.utils.FontUtils;

/**
 * Created by Kvest on 02.02.2015.
 */
public class MainMenuAdapter extends BaseAdapter {
    private static final int EMPTY_SELECTED_POSITION = -1;
    private static final int VIEW_TYPE_COUNT = 3;
    private static final MainMenuItem[] items = {
            new CategoryItem(R.string.menu_category_event),
            new DividerItem(),
            new SubcategoryItem(MainMenuFragment.MENU_FILMS_ID, R.string.menu_films, R.drawable.ic_menu_films, true),
            new SubcategoryItem(MainMenuFragment.MENU_CONCERT_ID, R.string.menu_concert, R.drawable.ic_menu_concert, false),
            new SubcategoryItem(MainMenuFragment.MENU_PARTY_ID, R.string.menu_party, R.drawable.ic_menu_party, false),
            new SubcategoryItem(MainMenuFragment.MENU_SPECTACLE_ID, R.string.menu_spectacle, R.drawable.ic_menu_spectacle, false),
            new SubcategoryItem(MainMenuFragment.MENU_EXHIBITION_ID, R.string.menu_exhibition, R.drawable.ic_menu_exhibition, false),
            new SubcategoryItem(MainMenuFragment.MENU_SPORT_ID, R.string.menu_sport, R.drawable.ic_menu_sport, false),
            new SubcategoryItem(MainMenuFragment.MENU_WORKSHOP_ID, R.string.menu_workshop, R.drawable.ic_menu_workshop, false),
            new DividerItem(),
            new CategoryItem(R.string.menu_category_place),
            new DividerItem(),
            new SubcategoryItem(MainMenuFragment.MENU_CINEMA_ID, R.string.menu_cinema, R.drawable.ic_menu_cinema, true),
            new SubcategoryItem(MainMenuFragment.MENU_THEATRE_ID, R.string.menu_theatre, R.drawable.ic_menu_theatre, false),
            new SubcategoryItem(MainMenuFragment.MENU_CONCERT_HALL_ID, R.string.menu_concert_hall, R.drawable.ic_menu_concert_hall, false),
            new SubcategoryItem(MainMenuFragment.MENU_CLUB_ID, R.string.menu_club, R.drawable.ic_menu_club, false),
            new SubcategoryItem(MainMenuFragment.MENU_MUSEUM_ID, R.string.menu_museum, R.drawable.ic_menu_museum, false),
            new SubcategoryItem(MainMenuFragment.MENU_GALLERY_ID, R.string.menu_gallery, R.drawable.ic_menu_gallery, false),
            new SubcategoryItem(MainMenuFragment.MENU_ZOO_ID, R.string.menu_zoo, R.drawable.ic_menu_zoo, false),
            new SubcategoryItem(MainMenuFragment.MENU_QUEST_ID, R.string.menu_quest, R.drawable.ic_menu_quest, false),
            new SubcategoryItem(MainMenuFragment.MENU_RESTAURANT_ID, R.string.menu_restaurant, R.drawable.ic_menu_restaurant, false),
            new SubcategoryItem(MainMenuFragment.MENU_CAFE_ID, R.string.menu_cafe, R.drawable.ic_menu_cafe, false),
            new SubcategoryItem(MainMenuFragment.MENU_PIZZA_ID, R.string.menu_pizza, R.drawable.ic_menu_pizza, false),
            new SubcategoryItem(MainMenuFragment.MENU_SUSHI_ID, R.string.menu_sushi, R.drawable.ic_menu_sushi, false),
            new SubcategoryItem(MainMenuFragment.MENU_KARAOKE_ID, R.string.menu_karaoke, R.drawable.ic_menu_karaoke, false),
            new SubcategoryItem(MainMenuFragment.MENU_SKATING_RINK_ID, R.string.menu_skating_rink, R.drawable.ic_menu_skating_rink, false),
            new SubcategoryItem(MainMenuFragment.MENU_BOWLING_ID, R.string.menu_bowling, R.drawable.ic_menu_bowling, false),
            new SubcategoryItem(MainMenuFragment.MENU_BILLIARD_ID, R.string.menu_billiard, R.drawable.ic_menu_billiard, false),
            new SubcategoryItem(MainMenuFragment.MENU_SAUNA_ID, R.string.menu_sauna, R.drawable.ic_menu_sauna, false),
            new SubcategoryItem(MainMenuFragment.MENU_BATH_ID, R.string.menu_bath, R.drawable.ic_menu_bath, false)
    };

    private int selectedItemPosition;
    private Context context;
    private int disabledSubcategoryTextColor, subcategoryTextColor, selectedSubcategoryTextColor;
    private int disabledSubcategoryIconTintColor, subcategoryIconTintColor, selectedSubcategoryIconTintColor;
    private Drawable subcategoryBg, selectedSubcategoryBg;
    private Typeface helveticaneuecyrRoman, helveticaneuecyrBold;

    public MainMenuAdapter(Context context) {
        super();

        this.context = context;
        selectedItemPosition = EMPTY_SELECTED_POSITION;

        initResources();
    }

    private void initResources() {
        // The attributes you want retrieved
        int[] attrs = {R.attr.MainMenuSubcategoryTextColor,
                       R.attr.MainMenuDisabledSubcategoryTextColor,
                       R.attr.MainMenuSelectedSubcategoryTextColor,
                       R.attr.MainMenuSubcategoryIconTintColor,
                       R.attr.MainMenuDisabledSubcategoryIconTintColor,
                       R.attr.MainMenuSelectedSubcategoryIconTintColor,
                       R.attr.MainMenuSubcategoryBg,
                       R.attr.MainMenuSelectedSubcategoryBg};

        // Parse style, using Context.obtainStyledAttributes()
        TypedArray ta = context.obtainStyledAttributes(attrs);

        try {
            // Fetching the resources defined in the style
            subcategoryTextColor = ta.getColor(0, Color.BLACK);
            disabledSubcategoryTextColor = ta.getColor(1, Color.BLACK);
            selectedSubcategoryTextColor = ta.getColor(2, Color.BLACK);
            subcategoryIconTintColor = ta.getColor(3, Color.BLACK);
            disabledSubcategoryIconTintColor = ta.getColor(4, Color.BLACK);
            selectedSubcategoryIconTintColor = ta.getColor(5, Color.BLACK);
            subcategoryBg = ta.getDrawable(6);
            selectedSubcategoryBg = ta.getDrawable(7);
        } finally {
            ta.recycle();
        }

        //retrieve font
        helveticaneuecyrRoman = FontUtils.getFont(context.getAssets(), FontUtils.HELVETICANEUECYR_ROMAN_FONT);
        helveticaneuecyrBold = FontUtils.getFont(context.getAssets(), FontUtils.HELVETICANEUECYR_BOLD_FONT);
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
                //specific properties
                if (item.enable) {
                    if (position == selectedItemPosition) {
                        bindSelectedSubcategory((SubcategoryViewHolder) view.getTag(), (SubcategoryItem) item);
                    } else {
                        bindSubcategory((SubcategoryViewHolder) view.getTag(), (SubcategoryItem) item);
                    }
                } else {
                    bindDisabledSubcategory((SubcategoryViewHolder) view.getTag(), (SubcategoryItem) item);
                }
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
                TextView categoryView = (TextView) LayoutInflater.from(context).inflate(R.layout.main_menu_item_category, parent, false);
                categoryView.setTypeface(helveticaneuecyrBold);
                return categoryView;
            case MainMenuItem.TYPE_SUBCATEGORY :
                View view  = LayoutInflater.from(context).inflate(R.layout.main_menu_item_subcategory, parent, false);
                view.setTag(new SubcategoryViewHolder(view));
                return view;
        }

        return null;
    }

    private void bindCategoryView(TextView view, CategoryItem item) {
        view.setText(item.textRes);
    }

    private void bindDisabledSubcategory(SubcategoryViewHolder holder, SubcategoryItem item) {
        //text
        holder.text.setTypeface(helveticaneuecyrRoman);
        holder.text.setTextColor(disabledSubcategoryTextColor);
        holder.text.setText(item.textRes);

        //icon
        holder.icon.setImageResource(item.iconRes);
        holder.icon.setColorFilter(disabledSubcategoryIconTintColor);
        holder.icon.setBackgroundDrawable(null);

        holder.disableOverlay.setVisibility(View.VISIBLE);
    }

    private void bindSubcategory(SubcategoryViewHolder holder, SubcategoryItem item) {
        //text
        holder.text.setTypeface(helveticaneuecyrRoman);
        holder.text.setTextColor(subcategoryTextColor);
        holder.text.setText(item.textRes);

        //icon
        holder.icon.setImageResource(item.iconRes);
        holder.icon.setColorFilter(subcategoryIconTintColor);
        holder.icon.setBackgroundDrawable(subcategoryBg);

        holder.disableOverlay.setVisibility(View.INVISIBLE);
    }

    private void bindSelectedSubcategory(SubcategoryViewHolder holder, SubcategoryItem item) {
        //text
        holder.text.setTypeface(helveticaneuecyrBold);
        holder.text.setTextColor(selectedSubcategoryTextColor);
        holder.text.setText(item.textRes);

        //icon
        holder.icon.setImageResource(item.iconRes);
        holder.icon.setColorFilter(selectedSubcategoryIconTintColor);
        holder.icon.setBackgroundDrawable(selectedSubcategoryBg);

        holder.disableOverlay.setVisibility(View.INVISIBLE);
    }

    public static class MainMenuItem {
        public static final int TYPE_CATEGORY = 0;
        public static final int TYPE_SUBCATEGORY = 1;
        public static final int TYPE_DIVIDER = 2;

        private int id;
        private int type;
        public boolean enable;

        public MainMenuItem(int id, int type, boolean enable) {
            this.id = id;
            this.type = type;
            this.enable = enable;
        }
    }

    private static class DividerItem extends MainMenuItem {
        public DividerItem() {
            super(-1, TYPE_DIVIDER, false);
        }
    }

    private static class CategoryItem extends MainMenuItem {
        public int textRes;

        public CategoryItem(int textRes) {
            super(-1, TYPE_CATEGORY, false);

            this.textRes = textRes;
        }
    }

    private static class SubcategoryItem extends MainMenuItem {
        public int textRes;
        public int iconRes;

        public SubcategoryItem(int id, int textRes, int iconRes, boolean enable) {
            super(id, TYPE_SUBCATEGORY, enable);
            this.textRes = textRes;
            this.iconRes = iconRes;
        }
    }

    private class SubcategoryViewHolder {
        public TextView text;
        public ImageView icon;
        public TextView disableOverlay;

        public SubcategoryViewHolder(View view) {
            text = (TextView) view.findViewById(R.id.text);
            icon = (ImageView) view.findViewById(R.id.icon);
            disableOverlay = (TextView) view.findViewById(R.id.disable_overlay);
            disableOverlay.setTypeface(helveticaneuecyrRoman);
        }
    }
}
