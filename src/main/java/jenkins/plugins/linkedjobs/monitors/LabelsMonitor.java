package jenkins.plugins.linkedjobs.monitors;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.model.Computer;
import hudson.model.Label;
import hudson.model.LabelFinder;
import hudson.model.Node;
import hudson.model.labels.LabelAtom;
import hudson.node_monitors.AbstractNodeMonitorDescriptor;
import hudson.node_monitors.NodeMonitor;

public class LabelsMonitor extends NodeMonitor
{

  private boolean includeDynamic = false;
  private int maxLabelCount = 5;

  public LabelsMonitor() {}

  @DataBoundConstructor
  public LabelsMonitor(boolean includeDynamic, int maxLabelCount)
  {
    this.includeDynamic = includeDynamic;
    this.maxLabelCount = maxLabelCount;
  }

  @Override
  public String getColumnCaption()
  {
    if (!isIgnored())
      return "Labels";
    return null;
  }

  public boolean getIncludeDynamic()
  {
    return includeDynamic;
  }

  public int getMaxLabelCount()
  {
    return maxLabelCount;
  }

  @Override
  public Object data(Computer c) {
    Node n = c.getNode();
    Set<LabelAtom> r = Collections.emptySet();
    if (n != null)
    {
      r = Label.parse(n.getLabelString());
      if (includeDynamic)
      {
        r.addAll(getDynamicLabels(n));
      }
    }
    return new Data(c, r);
  }

  private HashSet<LabelAtom> getDynamicLabels(Node n) {
    HashSet<LabelAtom> result = new HashSet<LabelAtom>();
    for (LabelFinder labeler : LabelFinder.all()) {
        // Filter out any bad(null) results from plugins
        // for compatibility reasons, findLabels may return LabelExpression and not atom.
        for (Label label : labeler.findLabels(n))
            if (label instanceof LabelAtom) result.add((LabelAtom)label);
    }
    return result;
  }

  public class Data
  {
    private Computer c;
    private Set<LabelAtom> labels;

    private Data(Computer c, Set<LabelAtom> labels)
    {
      this.c = c;
      this.labels = labels;
    }

    public Set<LabelAtom> getLabels()
    {
      return labels;
    }

    public Computer getComputer()
    {
      return c;
    }

    @Override
    public String toString()
    {
      return "" + labels.size();
    }
  }

  @Extension
  @Symbol("labels")
  public static class DescriptorImpl extends AbstractNodeMonitorDescriptor<Set<LabelAtom>> {

    @Override
    public String getDisplayName() {
        return "Labels";
    }

    @Override
    protected Set<LabelAtom> monitor(Computer c) throws IOException, InterruptedException
    {
      return null;
    }
  }
}
