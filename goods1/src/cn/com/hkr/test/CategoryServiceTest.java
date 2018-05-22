package cn.com.hkr.test;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.junit.Test;

import cn.com.hkr.commons.CommonUtils;
import cn.com.hkr.goods.category.domain.Category;
import cn.com.hkr.goods.category.service.CategoryService;
import cn.com.hkr.jdbc.TxQueryRunner;

public class CategoryServiceTest {

	@Test
	public void testfindAll() throws SQLException{
		
		TxQueryRunner qr = new TxQueryRunner();
		CategoryService cate = new CategoryService();
		List<Category> all = cate.findAll();//得到父类
		Iterator<Category> it = all.iterator();
		List<Category> list_all = new ArrayList<Category>();//提取父类里的子类
		while (it.hasNext()) {list_all.addAll(it.next().getChildren());}
		String sql3 = "select * from t_category where pid is null order by orderBy";
		List<Category> cate_parent = qr.query(sql3,new BeanListHandler<Category>(Category.class));
		Iterator<Category> it3 = cate_parent.iterator();
		List<Category> child = new ArrayList<Category>();
		while (it3.hasNext()) {
				String cid = it3.next().getCid();
				String sql = "select  * from t_category where pid  = ? order by orderBy";
				child.addAll(qr.query(sql, new BeanListHandler<Category>(Category.class),cid));
		}
		//先比较一级，在比较二级
		System.out.println(all.size() + "  " +  cate_parent.size() + "  "  +   list_all.size()   + "    "  +  child.size() );
		if (all.size() ==  cate_parent.size() &&  list_all.size() == child.size()) {
			while (it3.hasNext()) {
				Category  cate_all = it.next();
				Category  parent = it3.next();
				if (!(cate_all.getCid().equals(parent.getCid())? cate_all.getCname().equals(parent.getCname()) ? cate_all.getDesc().equals(parent.getDesc()) ?
						true:false:false:false)) {
					assertTrue(false);
				}
			}
			Iterator<Category> it_child = child.iterator();
			Iterator<Category> it_ra = list_all.iterator();
			while (it_child.hasNext()) {
				Category  cate1 = it_child.next();
				Category  parent1 = it_ra.next();
				if (!(cate1.getCid().equals(parent1.getCid())? cate1.getCname().equals(parent1.getCname()) ? cate1.getDesc().equals(parent1.getDesc()) ? 
					true:false:false:false)) {
					assertTrue(false);
				}
			}
		}else{
			assertTrue(false);
		}	
	}
}
