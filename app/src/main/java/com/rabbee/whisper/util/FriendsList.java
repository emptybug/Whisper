package com.rabbee.whisper.util;

import com.rabbee.whisper.R;
import com.rabbee.whisper.obj.User;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Rabbee on 2016/7/12.
 */
public class FriendsList {

    private List<Map<String , Object>> list = new LinkedList<>();

    public int getSize()
    {
        return list.size();
    }

    public Map<String , Object> add(User friend)
    {
        Map<String, Object> item = new HashMap<String, Object>();
        item.put("header", R.drawable.header_normal);
        item.put("name", friend.getName());
        item.put("ip", friend.getIp());
        item.put("user", friend);
        list.add(item);
        return item;
    }

    public Map<String , Object> remove(User friend)
    {
        Map<String , Object> map = find(friend);
        if(map != null)
        {
            list.remove(map);
        }
        return map;
    }

    public Map<String , Object> find(User friend)
    {
        for (Map<String , Object> map : list)
        {
            if(map.get("ip").equals(friend.getIp()))
            {
                return map;
            }
        }
        return null;
    }

    //  把此联系人提前到列表第一位
    public Map<String , Object> setFirst(User friend)
    {
        Map<String , Object> map = remove(friend);
        if(map != null)
        {
            list.add(0, map);
        }
        return map;
    }

    public List<Map<String, Object>> getList() {
        return list;
    }
}
