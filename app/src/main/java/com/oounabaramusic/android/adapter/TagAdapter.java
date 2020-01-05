package com.oounabaramusic.android.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.util.DensityUtil;
import com.oounabaramusic.android.widget.gridlayout.GridLayoutTagGrid;
import com.oounabaramusic.android.widget.textview.TextViewCell;

import java.util.Random;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder> implements View.OnClickListener{

    public static final int SELECT=1;               //选中
    public static final int INVERT_SELECTION=2;     //反选
    public static final int SELECT_FAIL=3;          //选中失败
    private Activity activity;
    private boolean[][][] selected=new boolean[100][100][GridLayoutTagGrid.COL_COUNT];
    private int selectedCnt=0;
    private Random random=new Random(47);
    private TextView tagCnt;

    public  TagAdapter(Activity activity,TextView tagCnt){
        this.activity=activity;
        this.tagCnt=tagCnt;
    }

    public int selectCell(int position,int i,int j){
        if(selected[position][i][j]){
            selected[position][i][j]=false;
            selectedCnt--;
            return INVERT_SELECTION;
        }else{
            if(selectedCnt==3){
                return SELECT_FAIL;
            } else{
                selected[position][i][j]=true;
                selectedCnt++;
                return SELECT;
            }
        }
    }

    public boolean isSelected(int position,int i,int j){
        return selected[position][i][j];
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder vh=new ViewHolder(new GridLayoutTagGrid(activity));
        return vh;
    }


    /**
     * 根据子标签的个数重新计算grid显示高度
     * 如果grid中的子View足够则直接用，并且隐藏多余的子View
     * 如果不够就new新的子View添加到grid中
     *
     * @param holder    viewHolder
     * @param position  位置
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GridLayoutTagGrid grid=holder.grid;

        int cnt=position+1;//selector_play_all,小标签的个数
        reSetHeight(grid,cnt);

        GridLayout.Spec rowSpec,columnSpec;
        GridLayout.LayoutParams params;
        //加入小tag

        //获取子View个数
        int childCount=grid.getChildCount();

        //够用
        if(childCount>cnt){
            int k=1;
            for(int i=0;i<grid.getRowCount();i++){                   //i 行数       j 列数       k 子View下标
                for(int j=0;j<GridLayoutTagGrid.COL_COUNT;j++){
                    if(i==0&&j==0||i==1&&j==0)
                        continue; //跳过大标签
                    TextViewCell tvc= (TextViewCell) grid.getChildAt(k);
                    if(i<needRow(cnt)){
                        tvc.setPosition(position);
                        tvc.setSelected(selected[position][i][j]);//用于判断单元格是否已选中
                        if(k<=cnt){
                            tvc.setText("小");
                        }else{
                            tvc.setText("");
                        }
                        tvc.setVisibility(View.VISIBLE);
                    }else{
                        tvc.setVisibility(View.GONE);
                    }
                    k++;
                }
            }
        //不够用
        }else{
            if(cnt>GridLayoutTagGrid.COL_COUNT*2-2){
                reSetRowCount(grid,cnt);
            }
            int k=1;
            for(int i=0;i<grid.getRowCount();i++){
                for(int j=0;j<GridLayoutTagGrid.COL_COUNT;j++){
                    if(i==0&&j==0||i==1&&j==0)
                        continue;//跳过大标签
                    if(k<childCount){
                        TextViewCell tvc= (TextViewCell) grid.getChildAt(k);
                        tvc.setPosition(position);
                        tvc.setSelected(selected[position][i][j]);
                        tvc.setText("小");
                        tvc.setVisibility(View.VISIBLE);
                    }else{
                        rowSpec = GridLayout.spec(i, 1.0f);
                        columnSpec = GridLayout.spec(j, 1.0f);
                        params = new GridLayout.LayoutParams(rowSpec, columnSpec);
                        params.height=0;
                        params.width=0;
                        if(k<=cnt){
                            grid.addView(new TextViewCell(activity,"小",i,j,position,this),params);
                        }else{
                            grid.addView(new TextViewCell(activity,"",i,j,position,this),params);
                        }
                    }
                    k++;
                }
            }
        }

        //设置大标签的图标和名字
        holder.icon.setImageResource(R.mipmap.isplaying);
        holder.name.setText("名字");
    }


    /**
     * 设置显示所需行数所需的grid高度
     * @param grid   grid
     * @param cnt    小标签个数
     */
    private void reSetHeight(GridLayoutTagGrid grid,int cnt){
        int height;
        height=DensityUtil.dip2px(activity,needRow(cnt)*GridLayoutTagGrid.ROW_HEIGHT);
        ViewGroup.LayoutParams lp=grid.getLayoutParams();
        lp.height=height;
        grid.setLayoutParams(lp);
    }

    /**
     * 如果需要的行数超过了现在的行数，重新设置grid行数
     * @param grid    grid
     * @param cnt     小标签的个数
     */
    private void reSetRowCount(GridLayoutTagGrid grid,int cnt){
        int rowCount=needRow(cnt);
        grid.setRowCount(rowCount);
    }

    /**
     * 计算显示cnt个小标签需要多少行
     * @param cnt  小标签的个数
     * @return     需要的行数
     */
    private int needRow(int cnt){
        if(cnt<=GridLayoutTagGrid.COL_COUNT*2-2){
            return 2;
        }else{
            return 2+((cnt-2*GridLayoutTagGrid.COL_COUNT+2+GridLayoutTagGrid.COL_COUNT-1)/GridLayoutTagGrid.COL_COUNT);
        }
    }

    @Override
    public int getItemCount() {
        return 20;
    }

    @Override
    public void onClick(View v) {
        if(v instanceof TextViewCell){
            TextViewCell cell= (TextViewCell) v;
            switch (selectCell(cell.getPosition(),cell.getRow(),cell.getCol())){
                case SELECT:
                    cell.setBackground(activity.getDrawable(R.drawable.gridlayout_cell_selected));
                    break;
                case INVERT_SELECTION:
                    cell.setBackground(cell.getNotSelect());
                    break;
                case SELECT_FAIL:
                    Toast.makeText(activity,"最多只可以3个标签",Toast.LENGTH_LONG).show();
                    break;
            }
            String sb = "请选择合适的标签，最多选择3个，已选" + selectedCnt + "个";
            tagCnt.setText(sb);
        }
    }

    class ViewHolder extends  RecyclerView.ViewHolder{
        GridLayoutTagGrid grid;
        ImageView icon;//大标签的图标
        TextView name;//大标签的名字

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            grid= (GridLayoutTagGrid) itemView;

            View view= LayoutInflater.from(activity).inflate(R.layout.special_big_tag_layout,null);
            icon=view.findViewById(R.id.tag_icon);
            name=view.findViewById(R.id.tag_name);
            view.setBackground(activity.getDrawable(R.drawable.gridlayout_cell_right));

            //加入大tag
            GridLayout.Spec rowSpec=GridLayout.spec(0,2,1f);
            GridLayout.Spec columnSpec=GridLayout.spec(0,1,1f);
            GridLayout.LayoutParams params=new GridLayout.LayoutParams(rowSpec,columnSpec);
            params.height=0;
            params.width=0;
            grid.addView(view,params);
        }
    }
}
