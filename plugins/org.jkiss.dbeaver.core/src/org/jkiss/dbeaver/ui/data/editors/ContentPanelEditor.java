/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2016 Serge Rieder (serge@jkiss.org)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License (version 2)
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.jkiss.dbeaver.ui.data.editors;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.*;
import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.Log;
import org.jkiss.dbeaver.core.DBeaverUI;
import org.jkiss.dbeaver.model.DBUtils;
import org.jkiss.dbeaver.model.data.DBDContent;
import org.jkiss.dbeaver.model.data.DBDContentStorage;
import org.jkiss.dbeaver.model.exec.DBCException;
import org.jkiss.dbeaver.model.impl.BytesContentStorage;
import org.jkiss.dbeaver.model.impl.StringContentStorage;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.runtime.DBRRunnableWithProgress;
import org.jkiss.dbeaver.ui.DBeaverIcons;
import org.jkiss.dbeaver.ui.UIIcon;
import org.jkiss.dbeaver.ui.controls.imageview.ImageViewer;
import org.jkiss.dbeaver.ui.data.IValueController;
import org.jkiss.dbeaver.ui.editors.binary.BinaryContent;
import org.jkiss.dbeaver.ui.editors.binary.HexEditControl;
import org.jkiss.dbeaver.utils.ContentUtils;
import org.jkiss.dbeaver.utils.GeneralUtils;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;

/**
* ControlPanelEditor
*/
public class ContentPanelEditor extends BaseValueEditor<Control> {

    private static final Log log = Log.getLog(ContentPanelEditor.class);

    public ContentPanelEditor(IValueController controller) {
        super(controller);
    }

    @Override
    public void contributeActions(@NotNull IContributionManager manager, @NotNull IValueController controller) throws DBCException {
        manager.add(new ContentTypeSwitchAction());
        if (control instanceof ImageViewer) {
            ((ImageViewer)control).fillToolBar(manager);
            manager.add(new Separator());
        } else if (control instanceof HexEditControl) {
            manager.add(new Action("Switch Insert/Overwrite mode", DBeaverIcons.getImageDescriptor(UIIcon.CURSOR)) {
                    @Override
                    public void run() {
                        ((HexEditControl)control).redrawCaret(true);
                    }
                });
        }
    }

    @Override
    public void primeEditorValue(@Nullable final Object value) throws DBException
    {
        if (value == null) {
            log.warn("NULL content value. Must be DBDContent.");
            return;
        }
        DBeaverUI.runInUI(valueController.getValueSite().getWorkbenchWindow(), new DBRRunnableWithProgress() {
            @Override
            public void run(DBRProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                monitor.beginTask("Prime content value", 1);
                try {
                    DBDContent content = (DBDContent) value;
                    DBDContentStorage data = content.getContents(monitor);
                    if (control instanceof Text) {
                        monitor.subTask("Read text value");
                        Text text = (Text) control;
                        StringWriter buffer = new StringWriter();
                        if (data != null) {
                            try (Reader contentReader = data.getContentReader()) {
                                ContentUtils.copyStreams(contentReader, -1, buffer, monitor);
                            }
                        }
                        text.setText(buffer.toString());
                    } else if (control instanceof HexEditControl) {
                        monitor.subTask("Read binary value");
                        HexEditControl hexEditControl = (HexEditControl) control;
                        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                        if (data != null) {
                            try (InputStream contentStream = data.getContentStream()){
                                ContentUtils.copyStreams(contentStream, -1, buffer, monitor);
                            }
                        }
                        hexEditControl.setContent(buffer.toByteArray());
                    } else if (control instanceof ImageViewer) {
                        monitor.subTask("Read image value");
                        ImageViewer imageViewControl = (ImageViewer) control;
                        if (data != null) {
                            try (InputStream contentStream = data.getContentStream()) {
                                if (!imageViewControl.loadImage(contentStream)) {
                                    valueController.showMessage("Can't load image: " + imageViewControl.getLastError().getMessage(), true);
                                } else {
                                    valueController.showMessage("Image: " + imageViewControl.getImageDescription(), false);
                                }
                            }
                        } else {
                            imageViewControl.clearImage();
                        }
                    }
                } catch (Exception e) {
                    log.error(e);
                    // Clear contents
                    if (control instanceof Text) {
                        ((Text) control).setText("");
                    } else if (control instanceof HexEditControl) {
                        ((HexEditControl) control).setContent(new byte[0]);
                    } else if (control instanceof ImageViewer) {
                        ((ImageViewer) control).clearImage();
                    }
                    // Show error
                    valueController.showMessage(e.getMessage(), true);
                } finally {
                    monitor.done();
                }
            }
        });
    }

    @Override
    public Object extractEditorValue() throws DBException
    {
        final DBDContent content = (DBDContent) valueController.getValue();
        if (content == null) {
            log.warn("NULL content value. Must be DBDContent.");
        } else {
            DBeaverUI.runInUI(DBeaverUI.getActiveWorkbenchWindow(), new DBRRunnableWithProgress() {
                @Override
                public void run(DBRProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    monitor.beginTask("Read content value", 1);
                    try {
                        if (control instanceof Text) {
                            monitor.subTask("Read text value");
                            Text styledText = (Text) control;
                            content.updateContents(
                                monitor,
                                new StringContentStorage(styledText.getText()));
                        } else if (control instanceof HexEditControl) {
                            monitor.subTask("Read binary value");
                            HexEditControl hexEditControl = (HexEditControl) control;
                            BinaryContent binaryContent = hexEditControl.getContent();
                            ByteBuffer buffer = ByteBuffer.allocate((int) binaryContent.length());
                            try {
                                binaryContent.get(buffer, 0);
                            } catch (IOException e) {
                                log.error(e);
                            }
                            content.updateContents(
                                monitor,
                                new BytesContentStorage(buffer.array(), GeneralUtils.getDefaultFileEncoding()));
                        }
                    } catch (Exception e) {
                        throw new InvocationTargetException(e);
                    } finally {
                        monitor.done();
                    }
                }
            });
        }
        return content;
    }

    @Override
    protected Control createControl(Composite editPlaceholder)
    {
        DBDContent content = (DBDContent) valueController.getValue();
        if (ContentUtils.isTextContent(content)) {
            Text text = new Text(editPlaceholder, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);
            text.setEditable(!valueController.isReadOnly());
            text.setFont(JFaceResources.getFont(JFaceResources.TEXT_FONT));
            return text;
        } else {
            ImageDetector imageDetector = new ImageDetector(content);
            if (!DBUtils.isNullValue(content)) {
                DBeaverUI.runInUI(valueController.getValueSite().getWorkbenchWindow(), imageDetector);
            }

            if (imageDetector.isImage()) {
                return new ImageViewer(editPlaceholder, SWT.NONE);
            } else {
                return new HexEditControl(editPlaceholder, SWT.BORDER);
            }
        }
    }

    private static class ImageDetector implements DBRRunnableWithProgress {
        private final DBDContent content;
        private boolean isImage;

        private ImageDetector(DBDContent content)
        {
            this.content = content;
        }

        public boolean isImage()
        {
            return isImage;
        }

        @Override
        public void run(DBRProgressMonitor monitor) throws InvocationTargetException, InterruptedException
        {
            if (!content.isNull()) {
                try {
                    DBDContentStorage contents = content.getContents(monitor);
                    if (contents != null) {
                        try (InputStream contentStream = contents.getContentStream()) {
                            new ImageLoader().load(contentStream);
                        }
                        isImage = true;
                    }
                }
                catch (Exception e) {
                    // this is not an image
                    log.debug("Can't detect image type: " + e.getMessage());
                }
            }
        }
    }

    private class ContentTypeSwitchAction extends Action {
        private Menu menu;

        public ContentTypeSwitchAction() {
            super("Text", Action.AS_DROP_DOWN_MENU);
        }

        @Override
        public void runWithEvent(Event event)
        {
            if (event.widget instanceof ToolItem) {
                ToolItem toolItem = (ToolItem) event.widget;
                Menu menu = createMenu(toolItem);
                Rectangle bounds = toolItem.getBounds();
                Point point = toolItem.getControl().toDisplay(bounds.x, bounds.y + bounds.height);
                menu.setLocation(point.x, point.y);
                menu.setVisible(true);
            }
        }

        private Menu createMenu(ToolItem toolItem) {
            if (menu == null) {
                menu = new Menu(toolItem.getParent());
                new MenuItem(menu, SWT.NONE).setText("Text");
                new MenuItem(menu, SWT.NONE).setText("Image");
                new MenuItem(menu, SWT.NONE).setText("Hex");
            }
            return menu;
        }

    }
}
