<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div>
	<form id="label-form">
		<div class="left">
			<table id="image-wrapper" cellpadding="0" cellspacing="0">
				<tr><td>
				<div id="main-pic">
				<img src='<s:property value="item.picUrl"/>_310x310.jpg'/>
				</div>
				</td></tr>
			</table>
			<div id="options"></div>
		</div>
		<div class="right">
			<div id="labels">
				<s:iterator value="labelCategories">
						<h3><a href="#"><s:property value="name"/></a></h3>
						<div class="labels-panel">
							<s:iterator value="labels">
								<div class="label-item" title="双击将标签添加到主图"><img src='<s:property value="src"/>' width="100%" height="100%"/></div>
							</s:iterator>
						</div>
				</s:iterator>
			</div>
		</div>
	</form>
</div>

<script type="text/javascript">
	$("#labels").accordion();
	
	$(".label-item").dblclick(
		function() {
			var $label = $("img", this);
			var src = $label.attr('src');
			$label.removeAttr('width').removeAttr('height');
			var w = $label.width();
			var h = $label.height();
			$label.attr('width', '100%').attr('height', '100%');
			var width;
			var height;
			if (w >= h)
			{
				width = 100;
				height = h * 100 / w;
			}
			else
			{
				height = 100;
				width = w * 100 / h;
			}
			$("<div><img src='" + src + "' width='100%' height='100%'/></div>").appendTo("#main-pic")
			.css({
				width: width,
				height: height,
				border: '1px solid lightblue',
				position: 'absolute',
				top: 0,
				left: 0,
				cursor: 'move'
			}).draggable({
				containment : "#main-pic",
				scroll : false
			}).resizable({
				containment : "#main-pic",
				aspectRatio : true,
				minWidth : 20,
				minHeight : 20,
				maxWidth : 310,
				maxHeight : 310
			});
		});
</script>