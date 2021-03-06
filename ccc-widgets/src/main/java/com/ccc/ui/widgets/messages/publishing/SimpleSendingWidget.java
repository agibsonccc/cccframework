package com.ccc.ui.widgets.messages.publishing;

import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
@Component
@Scope("session")
@DependsOn("appSyc")

public class SimpleSendingWidget extends CustomComponent {

	@AutoGenerated
	private AbsoluteLayout mainLayout;
	@AutoGenerated
	private VerticalLayout massPublishDiv;
	@AutoGenerated
	private Button button_1;
	@AutoGenerated
	private Upload upload_1;
	@AutoGenerated
	private TextArea textArea_1;
	@AutoGenerated
	private NativeButton composeButton;
	@AutoGenerated
	private NativeSelect nativeSelect_1;
	@AutoGenerated
	private MenuBar menuBar_1;

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	/**
	 * The constructor should first build the main layout, set the
	 * composition root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the
	 * visual editor.
	 */
	public SimpleSendingWidget() {
		buildMainLayout();
		setCompositionRoot(mainLayout);

		// TODO add user code here
	}

	@AutoGenerated
	private AbsoluteLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		
		// menuBar_1
		menuBar_1 = new MenuBar();
		menuBar_1.setCaption("Add account");
		menuBar_1.setImmediate(false);
		menuBar_1.setWidth("100.0%");
		menuBar_1.setHeight("-1px");
		mainLayout.addComponent(menuBar_1,
				"top:0.0px;right:398.0px;left:162.0px;");
		
		// nativeSelect_1
		nativeSelect_1 = new NativeSelect();
		nativeSelect_1.setImmediate(false);
		nativeSelect_1.setWidth("200px");
		nativeSelect_1.setHeight("-1px");
		mainLayout.addComponent(nativeSelect_1, "top:0.0px;left:540.0px;");
		
		// composeButton
		composeButton = new NativeButton();
		composeButton.setCaption("Compose Message");
		composeButton.setImmediate(true);
		composeButton.setWidth("-1px");
		composeButton.setHeight("-1px");
		mainLayout.addComponent(composeButton, "top:0.0px;left:770.0px;");
		
		// massPublishDiv
		massPublishDiv = buildMassPublishDiv();
		mainLayout.addComponent(massPublishDiv, "top:23.0px;left:160.0px;");
		
		return mainLayout;
	}

	@AutoGenerated
	private VerticalLayout buildMassPublishDiv() {
		// common part: create layout
		massPublishDiv = new VerticalLayout();
		massPublishDiv.setImmediate(false);
		massPublishDiv.setWidth("-1px");
		massPublishDiv.setHeight("182px");
		massPublishDiv.setMargin(false);
		
		// textArea_1
		textArea_1 = new TextArea();
		textArea_1.setImmediate(false);
		textArea_1.setWidth("310px");
		textArea_1.setHeight("-1px");
		massPublishDiv.addComponent(textArea_1);
		
		// upload_1
		upload_1 = new Upload();
		upload_1.setImmediate(false);
		upload_1.setWidth("-1px");
		upload_1.setHeight("-1px");
		massPublishDiv.addComponent(upload_1);
		
		// button_1
		button_1 = new Button();
		button_1.setCaption("Publish to all");
		button_1.setImmediate(true);
		button_1.setWidth("-1px");
		button_1.setHeight("-1px");
		massPublishDiv.addComponent(button_1);
		
		return massPublishDiv;
	}

}
