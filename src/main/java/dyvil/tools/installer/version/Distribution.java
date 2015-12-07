package dyvil.tools.installer.version;

import dyvil.collection.List;
import dyvil.collection.Map;
import dyvil.tools.dpf.ast.Node;
import dyvil.tools.dpf.ast.NodeElement;
import dyvil.tools.dpf.ast.Property;
import dyvil.tools.dpf.converter.Expandable;
import dyvil.tools.dpf.visitor.NodeVisitor;
import dyvil.tools.parsing.Name;

public class Distribution extends Node implements Expandable
{
	protected boolean resolved;

	protected String distributionName;
	protected String fileName;
	protected String downloadURL;

	protected EnumSelection selected = EnumSelection.SELECTED;

	protected List<String> dependencies;

	public Distribution(Name name)
	{
		super(name);
	}

	public String getDistributionName()
	{
		return distributionName;
	}

	public String getFileName()
	{
		return fileName;
	}

	public String getDownloadURL()
	{
		return downloadURL;
	}

	public EnumSelection getSelected()
	{
		return selected;
	}

	public List<String> getDependencies()
	{
		return dependencies;
	}

	public void resolve()
	{
		if (this.resolved)
		{
			return;
		}

		this.resolved = true;

		this.distributionName = getStringProperty("name");
		this.fileName = getStringProperty("fileName");
		this.downloadURL = getStringProperty("download");

		this.dependencies = getListProperty("dependencies");

		String selected = getStringProperty("selected");
		if (selected != null)
		{
			switch (selected)
			{
			case "true":
				this.selected = EnumSelection.SELECTED;
				break;
			case "false":
				this.selected = EnumSelection.UNSELECTED;
				break;
			case "force":
			case "forced":
				this.selected = EnumSelection.FORCED;
				break;
			}
		}
	}

	private Property getProperty(String name)
	{
		return (Property) this.elements
				.find(nodeElement1 -> nodeElement1.getName().equals(name) && nodeElement1 instanceof Property);
	}

	private String getStringProperty(String name)
	{
		Property nodeElement = getProperty(name);
		return nodeElement != null ? nodeElement.getValue().toString() : null;
	}

	private List<String> getListProperty(String name)
	{
		Property property = getProperty(name);
		return (property != null) ? ((List<String>) property.getValue()).immutable() : List.fromNil();
	}

	@Override
	public NodeVisitor visitNode(Name name)
	{
		Distribution child = new Distribution(name);
		this.nodes.add(child);
		return child;
	}

	@Override
	public Object expand(Map<String, Object> mappings, boolean mutate)
	{
		Distribution distribution;
		if (mutate)
		{
			distribution = this;
		}
		else
		{
			distribution = new Distribution(this.name);
		}

		distribution.elements = (List<NodeElement>) Expandable.expand(distribution.elements, mappings, mutate);
		distribution.nodes = (List<Node>) Expandable.expand(distribution.nodes, mappings, mutate);
		return distribution;
	}
}
