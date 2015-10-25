package dyvil.tools.installer.util;

import dyvil.lang.Double;
import dyvil.lang.Float;
import dyvil.lang.Int;
import dyvil.lang.Long;

import dyvil.collection.List;
import dyvil.collection.Map;
import dyvil.collection.mutable.ArrayList;
import dyvil.collection.mutable.HashMap;
import dyvil.tools.dpf.visitor.*;
import dyvil.tools.parsing.Name;

abstract class ValueVisitorImplementation implements ValueVisitor
{
	protected abstract void visitObject(Object o);
	
	@Override
	public void visitInt(int v)
	{
		this.visitObject(Int.apply(v));
	}
	
	@Override
	public void visitLong(long v)
	{
		this.visitObject(Long.apply(v));
	}
	
	@Override
	public void visitFloat(float v)
	{
		this.visitObject(Float.apply(v));
	}
	
	@Override
	public void visitDouble(double v)
	{
		this.visitObject(Double.apply(v));
	}
	
	@Override
	public void visitString(String v)
	{
		this.visitObject(v);
	}
	
	@Override
	public void visitName(Name v)
	{
		this.visitObject(new NameAccess(v.qualified));
	}
	
	@Override
	public ListVisitor visitList()
	{
		List<Object> list = new ArrayList();
		return new ListVisitor()
		{
			@Override
			public ValueVisitor visitElement()
			{
				return new ValueVisitorImplementation()
				{
					@Override
					protected void visitObject(Object o)
					{
						list.add(o);
					}
				};
			}
			
			@Override
			public void visitEnd()
			{
				visitObject(list);
			}
		};
	}
	
	@Override
	public MapVisitor visitMap()
	{
		Map<Object, Object> map = new HashMap();
		return new MapVisitor()
		{
			private Object key;
			
			@Override
			public ValueVisitor visitKey()
			{
				return new ValueVisitorImplementation()
				{
					@Override
					protected void visitObject(Object o)
					{
						key = o;
					}
				};
			}
			
			@Override
			public ValueVisitor visitValue()
			{
				return new ValueVisitorImplementation()
				{
					@Override
					protected void visitObject(Object o)
					{
						map.put(key, o);
					}
				};
			}
			
			@Override
			public void visitEnd()
			{
				visitObject(map);
			}
		};
	}
	
	@Override
	public StringInterpolationVisitor visitStringInterpolation()
	{
		StringInterpolation interpolation = new StringInterpolation();
		this.visitObject(interpolation);
		return interpolation;
	}
	
	@Override
	public BuilderVisitor visitBuilder(Name name)
	{
		return null;
	}
	
	@Override
	public ValueVisitor visitValueAccess(Name v)
	{
		NameAccess access = new NameAccess(v.qualified);
		this.visitObject(access);
		return access;
	}
}
