package com.example.bookkeeping;

import android.util.Log;

import com.example.bookkeeping.util.ReflectUtil;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals (4, 2 + 2);
    }
    @Test
    public void test(){
        Field[] fields = R.drawable.class.getFields ();
        List<Map<String,Object>> list=new ArrayList<> ();
        for(int i=0;i<fields.length;i++){
            Map<String, Object> map =new HashMap<> ();
            Field field = fields[i];
            if(field.getName ().indexOf ("ic_edit_")!=-1){
                try {
                    map.put (ReflectUtil.getLastStrByPattern (field.getName (),"_"),field.getInt (R.drawable.class));
                    list.add (map);
                }catch (Exception e){
                    Log.d ("ExampleUnitTest",e.getMessage (),e);
                }
            }
        }
    }
}