package dyvil.tools.installer.util;

import dyvil.collection.Map;
import dyvil.tools.dpf.visitor.*;
import dyvil.tools.parsing.Name;

public class MapConverter implements NodeVisitor
{
	private Map<String, Object>	map;
	private String				name;
	
	public MapConverter(Map<String, Object> map)
	{
		this.map = map;
		this.name = "";
	}
	
	private MapConverter(Map<String, Object> map, String name)
	{
		this.map = map;
		this.name = "";
	}
	
	private String getName(Name name)
	{
		if (this.name.isEmpty())
		{
			return name.qualified;
		}
		return this.name + '.' + name.qualified;
	}
	
	@Override
	public NodeVisitor visitNode(Name name)
	{
		return new MapConverter(this.map, this.getName(name));
	}
	
	@Override
	public NodeVisitor visitNodeAccess(Name name)
	{
		return new MapConverter(this.map, this.getName(name));
	}
	
	@Override
	public ValueVisitor visitProperty(Name name)
	{
		return new ValueVisitorImplementation()
		{
			String propertyName = getName(name);
			
			@Override
			protected void visitObject(Object o)
			{
				map.put(propertyName, o);
			}
		};
	}
}
