package dyvil.tools.installer.util;

import dyvil.collection.Map;

public interface Expandable
{
	public Object expand(Map<String, Object> map);
}
