package sy.ks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import android.telephony.SmsManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;

import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    //主页面按钮的值
    private static final int guanyu=0x111;
    private static final int chushihua=0x112;
    private static final int anshijian=0x113;
    private static final int anhaoma=0x114;
    private static final int anxingming=0x115;
    //初始化变量
    SimpleAdapter simpleAdapter;
    ListView listView;
    List<Map<String, Object>> listitems;
    List<String> number;
    List<String> namelist;
    List<String> infolist;

    //刷新界面列表
    void onshow() {
        personManager.init();//初始化personList
        personManager.load();//尝试从文件中读取personList
        //获取每个Person对象的简要信息
        List[] simpleInfo =personManager.getNameList();
        number = simpleInfo[0];
        namelist = simpleInfo[1];
        infolist=simpleInfo[2];
        //将读取到的List转换为字符串数组
        String[] arr1 = namelist.toArray(new String[]{});
        String[] arr2 = number.toArray(new String[]{});
        String[] arr3 = infolist.toArray(new String[]{});
        //主页面的提示
        TextView mt = findViewById(R.id.maintext);
        //如果联系人为空，就提示没有联系人，否则无提示
        if (arr1.length == 0) {
            mt.setText("您还没有联系人，请添加");
        }else{mt.setText("");}
        //用于存放每个 联系人简要信息 的List
        listitems = new ArrayList<>();
        //给每个联系人的简要信息综合起来放到HashMap中，并将HashMap放到List中
        for (int i = 0; i < arr1.length; i++) {
            Map<String, Object> listitem = new HashMap<>();
            listitem.put("name", arr1[i]);
            listitem.put("number", arr2[i]);
            listitem.put("info",arr3[i]);
            listitems.add(listitem);
        }
        //给ListView添加的SimpleAdapter
        //SimpleAdapter的5个参数，
        // 第二个是一个List<Map<String ?>>类型的集合对象，其中每个Map对象生成一个列表项
        // 第三个是一个布局的ID，对应layout下person.xml
        // 第四个是一个String,包含HashMap的key
        // 第五个是一个一个int[]类型的数组，包含要填充的组件的ID
        this.simpleAdapter = new SimpleAdapter(this, listitems, R.layout.person, new String[]{"name", "number","info"},
                new int[]{R.id.name, R.id.number,R.id.simpleinfo});
        //listview，对应activity_main.xml中的list1
        this.listView = findViewById(R.id.list1);
        //给list1添加SimpleAdapter
        listView.setAdapter(simpleAdapter);
        //给listview设置ContenMenu长按菜单
        registerForContextMenu(listView);
        //给listview添加单机按下事件的监听器
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent();
                //利用intent传递id值
                intent.putExtra("id",position);
                //跳转到infoActivity
                intent.setClass(MainActivity.this,infoActivity.class);
                startActivity(intent);
            }
        });
    }
    //添加一个漂浮的按钮（添加按钮）
    protected  void addfb(){
        //获取xml中的flyAddButton
        FloatingActionButton fab = findViewById(R.id.fab);
        //给fab添加按下监听器
        fab.setOnClickListener(new View.OnClickListener() {
            //同上，但是传递值为-1
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.putExtra("id",-1);
                intent.setClass(MainActivity.this,infoActivity.class);
                startActivity(intent);
            }
        });
    }
    //页面被创建时执行，初始化主界面
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addfb();
        onshow();
    }
    //页面被返回时执行
    @Override
    protected void onResume() {
        super.onResume();
        onshow();//刷新内容
    }
    //给主页面的联系人添加的菜单
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        menu.setHeaderTitle("选择操作");
        menu.add(0,1,Menu.NONE,"电话");
        menu.add(0,2,Menu.NONE,"短信");
        menu.add(0,3,Menu.NONE,"编辑");
        menu.add(0,4,Menu.NONE,"删除");
        super.onCreateContextMenu(menu, v, menuInfo);
    }
    //主页面右上角的菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

          menu.add(0,guanyu,0,"关于");
          menu.add(0,chushihua,0,"初始化");
          SubMenu px =menu.addSubMenu("排序方式");
          px.add(0,anshijian,0,"按时间");
          px.add(0,anhaoma,0,"按号码");
          px.add(0,anxingming,0,"按姓名");
        return super.onCreateOptionsMenu(menu);
    }
    //菜单被选中时执行的动作
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){

                case guanyu:
                    new AlertDialog.Builder(this)
                            .setTitle("关于")
                            .setIcon(R.mipmap.ic_launcher)
                            .setMessage("长江大学\n计算机科学学院\n大数据31802班\n孙泳\njava课设")
                            .create().show();
                break;
                case chushihua:
                    personManager.makelist();
                    onshow();
                    break;

                case anshijian:
                    personManager.paixuMethod=0;
                    personManager.save();
                    onshow();
                    break;
                case anhaoma:
                    personManager.paixuMethod=1;
                    personManager.save();
                    onshow();
                    break;
                case anxingming:
                    personManager.paixuMethod=2;
                    personManager.save();
                    onshow();
                    break;
            }

        return super.onOptionsItemSelected(item);
    }
    //联系人被长按选中时执行的动作
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        //获取联系人的位置（即id）
        int pos=(int)listView.getAdapter().getItemId(menuInfo.position);
            //switch被按下的二级菜单
        switch (item.getItemId()) {
            case 1:
        //电话
                //如果没有电话的权限，就申请电话的权限
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.
                        permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this, new
                            String[]{ Manifest.permission.CALL_PHONE}, 1);
                }
            else{
                //跳转到系统电话界面，拨打选中号码
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + personManager.personList.get(pos).phoneNumber));
                startActivity(intent);
            }

                break;
            case 2:
        //短信
                //跳转的系统短信界面，对指定号码发短信
                Uri uri2 = Uri.parse("smsto:"+personManager.personList.get(pos).phoneNumber);
                Intent intentMessage = new Intent(Intent.ACTION_VIEW,uri2);
                startActivity(intentMessage);
                break;
            case 3:
        //编辑
                Intent intent=new Intent();
                intent.putExtra("id",pos);
                intent.setClass(MainActivity.this,infoActivity.class);
                startActivity(intent);
                break;
            case 4:
        //删除
                //对向量中指定位置的person执行删除操作
                personManager.personList.remove(pos);
                //删完保存
                personManager.save();
                //刷新主界面的列表
                onshow();
                break;
        }
        return super.onContextItemSelected(item);

    }
}
