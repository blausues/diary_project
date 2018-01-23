package com.example.student.diary_project;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.student.diary_project.vo.AllDiaryVO;

import java.util.List;

/**
 * Created by student on 2018-01-23.
 */

public class AllDiaryAdapter extends ArrayAdapter<AllDiaryVO>{
    private Context context;
    private int itemLayout;
    private List<AllDiaryVO> allDiaryVOList;

    public AllDiaryAdapter(@NonNull Context context, int resource, @NonNull List<AllDiaryVO> objects) {
        super(context, resource, objects);

        this.context = context;
        this.itemLayout = resource;
        this.allDiaryVOList = objects;
    }

    class AllDiaryHolder {
        TextView listDate;
        TextView listContent;
        TextView listTv;
    }


}
