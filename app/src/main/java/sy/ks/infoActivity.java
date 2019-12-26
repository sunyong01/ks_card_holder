package sy.ks;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import static sy.ks.personManager.personList;
import static sy.ks.personManager.setid;
import static sy.ks.personManager.tmp;

//文本框监听器
class editTextListener implements TextWatcher{
    int p=-1;//id
    int m=-1;//监听器类型（名字，电话.....）
    //构造器
    editTextListener(int i,int m){
        this.p=i;
        this.m=m;
    }
    //将文本框对Person对象所做出的改变存到Vector中的对应的Person对象中
    //如果p=-1 代表是新建不是修改，则改变tmp对象的内容，否则改变Vector中对应id的Person的值
    private void setinfo(Editable editable){
                    Person person;
            if (p==-1){
                    person = tmp;
            } else
                {
                    person = personManager.personList.get(p);}
            //将文本框的修改写入到Person对象中
            switch (m){
                case 1:
                    person.name=editable.toString();
                    break;
                case 2:
                    person.phoneNumber=editable.toString();
                    break;
                case 3:
                    person.mail=editable.toString();
                    break;
                case 4:
                    person.address=editable.toString();
                    break;
                case 5:
                    person.company=editable.toString();
                    break;
                case 6:
                    person.zhiwu=editable.toString();
                    break;
                case 7:
                    person.beizhu=editable.toString();
                    break;
                case 8:
                    person.qq=editable.toString();
                    break;
        }
    }
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
    //输入框内容改变后做出的反应
    @Override
    public void afterTextChanged(Editable editable) {
        Log.i("数据被改变", p+"  "+editable.toString());
        //将改变的数据写入到Person对象
        setinfo(editable);
    }

}
public class infoActivity extends AppCompatActivity {
//对输入的数字进行转化，如果输入的是-1则返回tmp否则返回对应的id的Person
    Person Input(int num){
        Person p ;
        if (num==-1)
        {
            //先把tmp指向一个新的对象，在令p指向tmp
            tmp=new Person();
            p=tmp;
        }
       else{
            //返回对应的id的Person
            p=personList.get(num);
        }
         return  p;
    }
    //显示一个人的具体信息
    void showPerson(final Person p){
        {
            //文本框
            EditText e1 = findViewById(R.id.editinfoname);
            EditText e2 = findViewById(R.id.editinfophone);
            EditText e3 = findViewById(R.id.editinfomail);
            EditText e4 = findViewById(R.id.editinfoaddress);
            EditText e5 = findViewById(R.id.editinfocompany);
            EditText e6 = findViewById(R.id.editinfozhiwu);
            EditText e7 = findViewById(R.id.editinfobeizhu);
            EditText e8 = findViewById(R.id.editinfoqq);
            //设置文本框的文本内容（与Person对象对应的）
            e1.setText(p.name);
            e2.setText(p.phoneNumber);
            e3.setText(p.mail);
            e4.setText(p.address);
            e5.setText(p.company);
            e6.setText(p.zhiwu);
            e7.setText(p.beizhu);
            e8.setText(p.qq);
            //给本文框添加监听器
            e1.addTextChangedListener(new editTextListener(p.id, 1));
            e2.addTextChangedListener(new editTextListener(p.id, 2));
            e3.addTextChangedListener(new editTextListener(p.id, 3));
            e4.addTextChangedListener(new editTextListener(p.id, 4));
            e5.addTextChangedListener(new editTextListener(p.id, 5));
            e6.addTextChangedListener(new editTextListener(p.id, 6));
            e7.addTextChangedListener(new editTextListener(p.id, 7));
            e8.addTextChangedListener(new editTextListener(p.id, 8));
        }
        //保存按钮
        Button savebtn=findViewById(R.id.save_button);
        //给保存按钮添加监听器
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override//按下
            public void onClick(View view) {

                      Log.i("save_btn_clicked","save_btn_clicked");
                        //如果填写的信息没有错误（checkInfo是检测对象信息的函数，返回的是字符串错误信息）
                    if (personManager.checkInfo(p).equals("")) {
                            //如果p的id为-1说明是新建的对象
                        if (p.id==-1){//添加到Vector

                            //设置id从0开始，连续
                            setid();
                            //设置p的id
                            p.id=personList.size();
                            //tmp加入到Vector中
                            personList.add(tmp);
                        }
                        //保存
                        personManager.save();//TODO 保存到文件
                        finish();//结束，返回上一个Activity
                    }
                    else {//如果出现了信息错误
                            Log.i("O",personManager.checkInfo(p));
                        //提示错误信息
                            Toast.makeText(infoActivity.this, personManager.checkInfo(p), Toast.LENGTH_SHORT).show();
                        //提示失败
                        Snackbar.make(view, "添加/修改失败！", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();}
                }
        });
    }

    @Override//当这个Activity被创建时
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        //顶栏
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //利用intent获取传递值（-1为新联系人，其他值为联系人的ID）
        Intent intent=getIntent();
        int i=intent.getIntExtra("id",0);
        //在控制台输出intent获取的值，调试用
        Log.i("intenthuoqu",String.valueOf(i));
        //根据i值创建页面
        showPerson(Input(i));
    }
}
