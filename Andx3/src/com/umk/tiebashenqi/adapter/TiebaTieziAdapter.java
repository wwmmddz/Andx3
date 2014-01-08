package com.umk.tiebashenqi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.lidroid.xutils.util.LogUtils;
import com.umk.andx3.R;
import com.umk.andx3.util.xutil.BitmapHelp;
import com.umk.andx3.view.ScrollingTextView;
import com.umk.andx3.view.X3ProgressBar;
import com.umk.tiebashenqi.config.Code;
import com.umk.tiebashenqi.entity.Tieba;
import com.umk.tiebashenqi.entity.Tiezi;
import com.umk.tiebashenqi.entity.TieziPicture;
import com.umk.tiebashenqi.lpi.TiebaLpi;
import com.umk.tiebashenqi.lpi.TieziPictureLpi;
import com.umk.tiebashenqi.util.TiebaUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
* @author Winnid
* @title 帖子预览数据集
* @version:1.0
* @since：13-12-6
*/
public class TiebaTieziAdapter extends BaseExpandableListAdapter {

    private ExpandableListView expandableListView;
    private Context mContext;
    private LayoutInflater mInflater = null;
    private List<Tiezi> mGroup = null;//改名为mGroup,类型改为List<Tiezi>
    private List<List<TieziPicture>>   mData = null;

    public TiebaTieziAdapter(Context ctx, ExpandableListView view, List<Tiezi> tieziList, List<List<TieziPicture>> pictureList) {
        expandableListView = view;
        mContext = ctx;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mGroup = tieziList;
        mData = pictureList;
    }

    public void setData(List<Tiezi> tieziList, List<List<TieziPicture>> list) {
        mGroup = tieziList;
        mData = list;
    }

    @Override
    public int getGroupCount() {
        return mData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mData.get(groupPosition).size();
    }

    @Override
    public List<TieziPicture> getGroup(int groupPosition) {
        return mData.get(groupPosition);
    }

    @Override
    public TieziPicture getChild(int groupPosition, int childPosition) {
        return mData.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder holder;
        final Tiezi group;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.group_item_tiezi, null);
            holder = new GroupViewHolder();
            holder.mGroupName = (ScrollingTextView) convertView.findViewById(R.id.group_name);
            holder.mGroupCount = (TextView) convertView.findViewById(R.id.group_count);
            holder.btn_group_fresh = (Button) convertView.findViewById(R.id.btn_group_fresh);
            holder.btn_group_favorite = (Button) convertView.findViewById(R.id.btn_group_favorite);
            convertView.setTag(holder);
        } else {
            holder = (GroupViewHolder) convertView.getTag();
        }
        group = mGroup.get(groupPosition);
        holder.mGroupName.setText(group.getTheName());
        holder.mGroupCount.setText("[" + mData.get(groupPosition).size() + "]");
        holder.btn_group_fresh.setFocusable(false);
        holder.btn_group_fresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                X3ProgressBar<List<TieziPicture>> x3ProgressBar = new X3ProgressBar<List<TieziPicture>>(mContext, "正在加载...", false, null, false) {
                    @Override
                    public List<TieziPicture> doWork() {
                        //TODO(OK):刷新当前帖子图片数据
                        //下载贴吧的每一页
                        HashSet<String> set = TiebaUtil.getDetailsPageImageList(group.getUrl());
                        List<TieziPicture> childList = new ArrayList<TieziPicture>();
                        for (String url : set) {
                            TieziPicture tieziPicture = new TieziPicture();
                            tieziPicture.setImageUrl(url);
                            childList.add(tieziPicture);
                        }
                        return childList;
                    }

                    @Override
                    public void doResult(List<TieziPicture> childList) {
                        mData.set(groupPosition, childList);
                        notifyDataSetChanged();
                        expandableListView.expandGroup(groupPosition);
                        //TODO(OK):保存数据到数据库
                        TieziPictureLpi tieziPictureLpi = new TieziPictureLpi();
                        tieziPictureLpi.saveOrUpdate(mContext, childList);
                    }
                };
                x3ProgressBar.start();

            }
        });
        holder.btn_group_favorite.setVisibility(View.GONE);
        holder.btn_group_favorite.setFocusable(false);
        holder.btn_group_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:加入收藏
                LogUtils.e("加入收藏");

            }
        });

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder holder;
        TieziPicture item;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.group_child_tiezi_picture, null);
            holder = new ChildViewHolder();
            holder.mImg = (ImageView) convertView.findViewById(R.id.img);
            convertView.setTag(holder);
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }
        item = getChild(groupPosition, childPosition);
        BitmapHelp.getBitmapUtils(mContext).display(holder.mImg, item.getImageUrl());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        /*很重要：实现ChildView点击事件，必须返回true*/
        return true;
    }

    private class GroupViewHolder {
        ScrollingTextView mGroupName;
        TextView mGroupCount;
        Button btn_group_fresh;
        Button btn_group_favorite;
    }

    private class ChildViewHolder {
        ImageView mImg;
    }
}
