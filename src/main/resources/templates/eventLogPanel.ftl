<#list events as event>
	<#switch event.type>
		<#case "INFO">
		  	<#-- icon cheat sheet http://fontawesome.io/cheatsheet/ use this page to file icon name  -->
		  	<#-- icon name fa-info-circle  -->
		  	<#assign icon="&#xf05a;" color="#"+event.baseColor >
		  	<#break>
		<#case "OUT">
		  	<#-- icon name fa-arrow-circle-left  -->
		    <#assign icon="&#xf0a8;" color="#"+event.outColor >
		  	<#break>
		<#case "IN">
		  	<#-- icon name fa-arrow-circle-right  -->
		    <#assign icon="&#xf0a9;" color="#"+event.inColor >
		    <#break>
		<#case "ERROR">
		  	<#-- icon name fa-times-circle  -->
		 	<#assign icon="&#xf057;" color="red" >	
		 	<#break>
		<#default>
			<#-- default icon is fa-exclamation-triangle with default color is black -->
			<#assign icon="&#xf071;" color="#"+event.baseColor>
	</#switch>
	
	<#-- build event log -->
	<div style="font-family:monospace; color:${color}; white-space: nowrap; overflow">
		<#if global>
			<span style="white-space: pre">[${event.system?right_pad(7)[0..*7]}]</span>
			<span style="white-space: pre">[${event.sourceID?left_pad(5)[0..*5]}]</span>
		</#if>
		<span>[${event.time}]<span>
		<span class="v-icon" style="font-family: FontAwesome;">${icon}</span>
		<span style="white-space: pre">[${event.payload}]</span>
		</br>
	</div>
</#list>