<#if smallFont>
	^XA^JMA^JUS^XZ
<#elseif bigFont>
	^XA^JMB^JUS^XZ
</#if>
^XA
^XFR:${templateFile}^FS
<#list fields as field>
^FN${field.key}
<#-- switch case is needed because if hex data is present, the template needs another freemarker statement -->
<#switch field.value.type>
 	<#case "CHAR">
 		^FD${field.value}
 		<#break>
 	<#case "HEX">
 		^FH^FD${field.value}
		<#break>
</#switch>
^FS
</#list>
^XZ
<#if smallFont>
	^XA^JMB^JUS^XZ
<#elseif bigFont>
	^XA^JMA^JUS^XZ
</#if>