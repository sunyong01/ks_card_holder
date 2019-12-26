package sy.ks;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import net.sourceforge.pinyin4j.PinyinHelper;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Person implements Serializable {
    int id = -1;
    String name = "";
    String phoneNumber = "";
    String mail = "";
    String address = "";
    String company = "";
    String zhiwu = "";
    String beizhu = "";
    String qq = "";
    Long date = System.currentTimeMillis();

    Person() {
    }

    Person(int id, String phoneNumber, String name, String company, String zhiwu) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.company = company;
        this.zhiwu = zhiwu;
    }

}

class check {
    static boolean checkEmpty(Person person) {
        return person.name.equals("") && person.phoneNumber.equals("");
    }

    static boolean checkEmail(String email) {
        String RULE_EMAIL = "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$";
//正则表达式的模式
        Pattern p = Pattern.compile(RULE_EMAIL);
//正则表达式的匹配器
        Matcher m = p.matcher(email);
//进行正则匹配
        return m.matches();
    }

    static boolean checkphone(String phone) {
        String RULE_PHONE1 = "\\d{3}-\\d{8}|\\d{4}-\\d{7}";
        String RULE_PHONE2 = "^((13[0-9])|(15[^4])|(18[0-9])|(17[0-8])|(147,145))\\d{8}$";
//正则表达式的模式
        Pattern p2 = Pattern.compile(RULE_PHONE2);
        Pattern p = Pattern.compile(RULE_PHONE1);

//正则表达式的匹配器
        Matcher m2 = p2.matcher(phone);
        Matcher m = p.matcher(phone);
//进行正则匹配
        return m2.matches() || m.matches();
    }

    static boolean checkqq(String qq) {
        //10000开始
        String RULE_QQ = "[1-9][0-9]{4,}";
        Pattern p = Pattern.compile(RULE_QQ);
        Matcher m = p.matcher(qq);
        return m.matches();
    }

}

class paixu {
    //对给定的排序方式对Vector执行排序
    static void paixu(Vector v, int i) {
        switch (i) {
            case 0://按创建时间
                Paixu_time(v);
                break;
            case 1://按电话
                Paixu_tel(v);
                break;
            case 2://按姓名
                Paixu_name(v);
                break;
        }
    }
    //按创建时间排序
    private static void Paixu_time(Vector v) {
        Collections.sort(v, new Comparator<Person>() {
            @Override
            public int compare(Person o, Person o2) {
                return o.date.compareTo(o2.date);
            }
        });
    }
    //按电话号码数字排序
    private static void Paixu_tel(Vector v) {
        Collections.sort(v, new Comparator<Person>() {
            @Override
            public int compare(Person o, Person o2) {
                return o.phoneNumber.compareTo(o2.phoneNumber);
                //如果o1<o2则返回-1
            }
        });
    }
    //按姓名拼音字母排序
    private static void Paixu_name(Vector v) {
        Collections.sort(v, new ComparatorPinYin() {
        });
    }
    //拼音排序的Comparator
    static class ComparatorPinYin implements Comparator<Person> {
        @Override
        public int compare(Person o1, Person o2) {
            return ToPinYinString(o1.name).compareTo(ToPinYinString(o2.name));
        }
        //输入中文，返回拼音字符串
        private String ToPinYinString(String str) {
            StringBuilder sb = new StringBuilder();
            String[] arr;
            for (int i = 0; i < str.length(); i++) {
                arr = PinyinHelper.toHanyuPinyinStringArray(str.charAt(i));
                if (arr != null && arr.length > 0) {
                    for (String string : arr) {
                        sb.append(string);
                    }
                }
            }
            //返回拼音字符串
            return sb.toString();
        }
    }
}

//管理Person的对象 Person管理器
public class personManager {
    static int paixuMethod = 0;//默认按时间调试
    static Person tmp = new Person();//tmp
    //打开文件
    static File file = new File(Environment.getDataDirectory().toString() + "/user/0/sy.ks/mingianfile.txt");
    static ObjectInputStream oin;//输入流
    static ObjectOutputStream oot;//输出流
    static Vector<Person> personList;//保存联系人信息的向量
    //初始化personList
    static void init() { personList = new Vector<>(); }
    //从文件读取personList
    static void load() {
        //如果文件不存在
        if (!file.exists()) {
            //先得到文件的上级目录，并创建上级目录，在创建文件
            file.getParentFile().mkdir();
            try {
                //创建文件
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //文件存在，尝试读取
        try {
            //输入流
            oin = new ObjectInputStream(new FileInputStream(file));
            personList = (Vector<Person>) oin.readObject();//读取，反序列化
            //关闭流
            oin.close();
        } catch (IOException e) {
            Log.i("IOExceptins", e.toString());
        } catch (ClassNotFoundException e) {
            Log.i("IOExceptins", e.toString());
        }
    }
    //保存联系人到文件
    static void save() {
        try {
            //输出流
            oot = new ObjectOutputStream(new FileOutputStream(file));
            //对personList执行paixuMethod类型的排序
            paixu.paixu(personList, paixuMethod);
            //重新设置ID
            setid();
            //序列化
            oot.writeObject(personList);
            //关闭流
            oot.close();
        } catch (IOException e) {
            Log.i("IOExceptins", e.toString());
        }
            load();
    }

    static void makelist() {
        //TODO 测试用的方法 先尝试从文件读取，如果文件不在联系人，就创建15个联系人并保存
        load();
        if (personList.size() == 0) {

            personList.add(0, new Person(0, "14591002565", "魏楠", "黄石金承传媒有限公司", ""));
            personList.add(1, new Person(1, "18814561287", "汪林", "巨奥科技有限公司", ""));
            personList.add(2, new Person(2, "18810171060", "张玲", "维涛网络有限公司", ""));
            personList.add(3, new Person(3, "13054153461", "陈静", "华泰通安传媒有限公司", ""));
            personList.add(4, new Person(4, "15563455218", "施英", "七喜网络有限公司", ""));
            personList.add(5, new Person(5, "18146960695", "张雪梅", "MBP软件传媒有限公司", ""));
            personList.add(6, new Person(6, "18532297563", "彭晶", "超艺信息有限公司", ""));
            personList.add(7, new Person(7, "18500016285", "江鹏", "雨林木风计算机传媒有限公司", ""));
            personList.add(8, new Person(8, "13848396656", "郑东", "华成育卓信息有限公司", ""));
            personList.add(9, new Person(9, "13558949535", "李晶", "时刻网络有限公司", ""));
            personList.add(10, new Person(10, "15803475051", "王雪", "超艺网络有限公司", ""));
            personList.add(11, new Person(11, "13386184817", "张瑜", "佳禾科技有限公司", ""));
            personList.add(12, new Person(12, "13371038368", "罗涛", "银嘉传媒有限公司", ""));
            personList.add(13, new Person(13, "15137485670", "郭丽丽", "富罳网络有限公司", ""));
            personList.add(14, new Person(14, "15285786196", "陈秀云", "盟新传媒有限公司", ""));

        }
        save();
    }

    static void setid() {
        //对Vector中的Person按照在Vector中的位置设置ID
        for (int i = 0; i < personList.size(); i++) {
            personList.get(i).id = i;
        }

    }
    //对输入的信息执行检测
    static String checkInfo(Person p) {

        if (check.checkEmpty(p)) {
            return "联系人电话/姓名为空";
        }
        if (!p.mail.equals("")) {
            if (!check.checkEmail(p.mail)) {
                return "邮箱格式错误";
            }
        }
        if (!p.qq.equals("")) {
            if (!check.checkqq(p.qq)) {
                return "QQ格式错误";
            }
        }
        if (!p.phoneNumber.equals("")) {
            if (!check.checkphone(p.phoneNumber)) {
                return "电话格式错误";
            }
        }
        return "";
    }
        //对应主界面的联系人列表，从Person对象中获取简要的信息，存在List中，返还给主界面函数
    static List[] getNameList() {
        Vector h = personManager.personList;
        List<String> l1 = new ArrayList<>();
        List<String> l2 = new ArrayList<>();
        List<String> l3 = new ArrayList<>();
        if (!h.isEmpty()) {
            for (int i = 0; i < h.size(); i++) {
                Person p = (Person) h.get(i);
                l1.add(p.phoneNumber);
                l2.add(p.name);
                l3.add(p.company + p.zhiwu);
                // Log.i("namelist",p.id+"dianhua = " + p.phoneNumber + ", mingzi = " + p.name+"xinxi= "+p.company+p.zhiwu);
            }
        }
        return new List[]{l1, l2, l3};
    }
}
