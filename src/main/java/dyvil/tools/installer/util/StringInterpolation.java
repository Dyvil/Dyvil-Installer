package dyvil.tools.installer.util;

import dyvil.collection.List;
import dyvil.collection.Map;
import dyvil.collection.mutable.ArrayList;
import dyvil.tools.dpf.visitor.StringInterpolationVisitor;
import dyvil.tools.dpf.visitor.ValueVisitor;

public class StringInterpolation implements StringInterpolationVisitor, Expandable
{
	private List<String>	strings	= new ArrayList();
	private List<Object>	values	= new ArrayList();
	
	@Override
	public void visitStringPart(String string)
	{
		this.strings.add(string);
	}
	
	@Override
	public ValueVisitor visitValue()
	{
		return new ValueVisitorImplementation()
		{
			@Override
			protected void visitObject(Object o)
			{
				values.add(o);
			}
		};
	}
	
	@Override
	public void visitEnd()
	{
	}
	
	@Override
	public Object expand(Map<String, Object> map)
	{
		StringBuilder builder = new StringBuilder(this.strings.get(0));
		
		int len = this.values.size();
		for (int i = 0; i < len; i++)
		{
			Object o = this.values.get(i);
			if (o instanceof Expandable)
			{
				o = ((Expandable) o).expand(map);
			}
			builder.append(o);
			builder.append(this.strings.get(i + 1));
		}
		
		return builder.toString();
	}
}
