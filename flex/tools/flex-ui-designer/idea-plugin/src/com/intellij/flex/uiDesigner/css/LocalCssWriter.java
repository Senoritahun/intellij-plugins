package com.intellij.flex.uiDesigner.css;

import com.intellij.flex.uiDesigner.AssetCounter;
import com.intellij.flex.uiDesigner.InjectionUtil;
import com.intellij.flex.uiDesigner.InvalidPropertyException;
import com.intellij.flex.uiDesigner.ProblemsHolder;
import com.intellij.flex.uiDesigner.io.StringRegistry;
import com.intellij.flex.uiDesigner.mxml.AmfExtendedTypes;
import com.intellij.flex.uiDesigner.mxml.ProjectComponentReferenceCounter;
import com.intellij.javascript.flex.css.FlexStyleIndexInfo;
import com.intellij.lang.javascript.psi.ecmal4.JSClass;
import com.intellij.psi.css.CssString;

public class LocalCssWriter extends CssWriter {
  private final ProjectComponentReferenceCounter projectComponentReferenceCounter;

  public LocalCssWriter(StringRegistry.StringWriter stringWriter, ProblemsHolder problemsHolder,
                        ProjectComponentReferenceCounter projectComponentReferenceCounter, AssetCounter assetCounter) {
    super(stringWriter, problemsHolder, assetCounter);
    this.projectComponentReferenceCounter = projectComponentReferenceCounter;
  }

  @Override
  protected void writeClassReference(JSClass jsClass, FlexStyleIndexInfo info, CssString cssString) throws InvalidPropertyException {
    final int projectComponentFactoryId;
    if (info != null && info.getAttributeName().equals("skinClass")) {
      projectComponentFactoryId = InjectionUtil.getProjectComponentFactoryId(jsClass, projectComponentReferenceCounter);
    }
    else if (InjectionUtil.isProjectComponent(jsClass)) {
      throw new InvalidPropertyException(cssString, "class.reference.in.css.support.only.skin.class", jsClass.getQualifiedName());
    }
    else {
      projectComponentFactoryId = -1;
    }

    if (projectComponentFactoryId == -1) {
      super.writeClassReference(jsClass, info, cssString);
    }
    else {
      propertyOut.write(AmfExtendedTypes.DOCUMENT_FACTORY_REFERENCE);
      propertyOut.writeUInt29(projectComponentFactoryId);
    }
  }
}
