package com.zengtengpeng.relation.oneToMany;

import com.zengtengpeng.autoCode.bean.BuildJavaMethod;
import com.zengtengpeng.autoCode.config.AutoCodeConfig;
import com.zengtengpeng.autoCode.utils.MyStringUtils;
import com.zengtengpeng.relation.bean.RelationTable;
import com.zengtengpeng.relation.build.BuildBaseServiceImpl;
import com.zengtengpeng.relation.config.RelationConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * 构建一对多serviceImpl
 */
@FunctionalInterface
public interface BuildOneToManyServiceImpl extends BuildBaseServiceImpl {
    /**
     * 构建主表 级联查询(带分页)
     * @return
     */
    default BuildJavaMethod primarySelect(RelationConfig relationConfig){
        RelationTable primary = relationConfig.getPrimary();
        RelationTable foreign = relationConfig.getForeign();
        AutoCodeConfig autoCodeConfig = relationConfig.getAutoCodeConfig();
        BuildJavaMethod javaMethod = new BuildJavaMethod();
        List<String> an = new ArrayList<>();
        an.add("@Override");
        String primaryKeyBeanName_ =primary.getBeanNameLower();
        String foreignBeanName = foreign.getBeanName();
        String foreignBeanNameLower = foreign.getBeanNameLower();
        String primaryKeyBeanName = primary.getBeanName();
        javaMethod.setAnnotation(an);
        javaMethod.setReturnType(primaryKeyBeanName);
        javaMethod.setMethodType("public");
        javaMethod.setMethodName(String.format("select%sAnd%s", primaryKeyBeanName, foreignBeanName));
        List<String> params=new ArrayList<>();
        params.add(primaryKeyBeanName +" "+ primaryKeyBeanName_);
        javaMethod.setParams(params);
        StringBuffer content=new StringBuffer();
        MyStringUtils.append(content,"%s = this.selectAllByPaging(%s);",primaryKeyBeanName_,primaryKeyBeanName_);
        MyStringUtils.append(content,"if(%s!=null && %s.getRows()!=null){",2,primaryKeyBeanName_,primaryKeyBeanName_);
        MyStringUtils.append(content,"%s.getRows().forEach(t->{",3,primaryKeyBeanName_);
        MyStringUtils.append(content,"%s data= (%s) t;",4,primaryKeyBeanName,primaryKeyBeanName);
        MyStringUtils.append(content,"%s %s=new %s();",4,foreignBeanName,foreignBeanNameLower,foreignBeanName);
        MyStringUtils.append(content,"%s.set%s(data.get%s());",4,foreignBeanNameLower,
                foreign.getForeignKeyUp(true),
                primary.getPrimaryKeyUp(true));
        MyStringUtils.append(content,"List<%s> lists = %s%s.selectByCondition(%s);",4,foreignBeanName,foreignBeanNameLower,autoCodeConfig.getGlobalConfig().getPackageDaoUp(),foreignBeanNameLower);
        MyStringUtils.append(content,"data.set%sList(lists);",4,foreignBeanName);
        MyStringUtils.append(content,"});",3);
        MyStringUtils.append(content,"}",2);
        MyStringUtils.append(content,"return %s;",2,primaryKeyBeanName_);
        javaMethod.setContent(content.toString());
        javaMethod.setRemark("级联查询(带分页) "+primary.getRemark()+"--"+foreign.getRemark());
        return javaMethod;
    }

}