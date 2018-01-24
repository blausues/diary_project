package com.example.student.diary_project;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.student.diary_project.vo.AllDiaryVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by student on 2018-01-23.
 */

public class AllDiaryAdapter extends BaseAdapter {
    //두개의 아이템 뷰를 하나의 리스트뷰에서 구현하기 위해 타입 지정
    public static final int ITEM_VIEW_TYPE_READ = 0;
    public static final int ITEM_VIEW_TYPE_WRITE = 1;
    public static final int ITEM_VIEW_TYPE_MAX = 2;
    /////////////////////////////////////////////////////////////////////
    private List<AllDiaryVO> allDiaryVOArrayList = new ArrayList<>();

    public  AllDiaryAdapter(){}

    class AllDiaryHolder {
        TextView listDate;
        TextView listContent;
        TextView listTv;
        TextView listAddDate;
        TextView listAddTv;
    }

    @Override
    public int getCount() {
        return allDiaryVOArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return allDiaryVOArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return ITEM_VIEW_TYPE_MAX;
    }

    @Override
    public int getItemViewType(int position) {
        return allDiaryVOArrayList.get(position).getType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();
        int viewType = getItemViewType(position);
        AllDiaryHolder holder;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        AllDiaryVO allDiaryVO = allDiaryVOArrayList.get(position);
        holder = new AllDiaryHolder();

        switch (viewType) {
            case ITEM_VIEW_TYPE_READ:
                convertView = inflater.inflate(R.layout.item_day, parent, false);
                holder.listDate = convertView.findViewById(R.id.list_date);
                holder.listContent = convertView.findViewById(R.id.list_content);
                holder.listTv = convertView.findViewById(R.id.list_tv);

                holder.listDate.setText(allDiaryVO.getWriteDate());
                holder.listContent.setText(allDiaryVO.getContent());
                if (allDiaryVO.getTheme() == 0) {
                    holder.listTv.setText("일반");
                } else if (allDiaryVO.getTheme() == 1) {
                    holder.listTv.setText("그림");
                } else if (allDiaryVO.getTheme() == 2) {
                    holder.listTv.setText("금연");
                } else {
                    holder.listTv.setText("다이어트");
                }
                break;
            case ITEM_VIEW_TYPE_WRITE:
                convertView = inflater.inflate(R.layout.item_plus, parent, false);
                holder.listAddDate = convertView.findViewById(R.id.list_add_date);
                holder.listAddTv = convertView.findViewById(R.id.list_add_tv);

                holder.listAddDate.setText(allDiaryVO.getWriteDate());
                if (allDiaryVO.getTheme() == 0) {
                    holder.listAddTv.setText("일반");
                } else if (allDiaryVO.getTheme() == 1) {
                    holder.listAddTv.setText("그림");
                } else if (allDiaryVO.getTheme() == 2) {
                    holder.listAddTv.setText("금연");
                } else {
                    holder.listAddTv.setText("다이어트");
                }
                break;
        }
        return convertView;
    }

    public void addRead(AllDiaryVO allDiaryVO) {
        AllDiaryVO allDiary = new AllDiaryVO();
        allDiary.setType(ITEM_VIEW_TYPE_READ);
        allDiary.setWriteDate(allDiaryVO.getWriteDate());
        allDiary.setContent(allDiaryVO.getContent());
        allDiary.setTheme(allDiaryVO.getTheme());

        allDiaryVOArrayList.add(allDiary);
    }

    public void addWrite(AllDiaryVO allDiaryVO) {
        AllDiaryVO allDiary = new AllDiaryVO();
        allDiary.setType(ITEM_VIEW_TYPE_WRITE);
        allDiary.setWriteDate(allDiaryVO.getWriteDate());
        allDiary.setTheme(allDiaryVO.getTheme());

        allDiaryVOArrayList.add(allDiary);
    }
/////////////////////////////////////////////////////////////////////////
    public void justAdd(AllDiaryVO allDiaryVO){
        allDiaryVOArrayList.add(allDiaryVO);
    }

    public void dataClear(){
        allDiaryVOArrayList.clear();
    }
}
