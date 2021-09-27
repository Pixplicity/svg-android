package com.pixplicity.sharp;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class StyleSet {
	private HashMap<String, String> attributeStyleMap = new HashMap<>();
    private HashMap<String, HashMap<String, String>> elementStyleMap = new HashMap<>();
    private HashMap<String, HashMap<String, String>> classHashMap = new HashMap<>();

    private StyleSet extraStyleSet;

    String getStyle(String styleAttributeName, String elementType, List<String> elementClasses) {

        // returns value that is defined in the style attribute of the element
        if (attributeStyleMap.containsKey(styleAttributeName)) {
            return attributeStyleMap.get(styleAttributeName);
        }

        // returns value that is defined in the class of the element
        if (elementClasses != null && elementClasses.size() > 0) {
            for(String className: elementClasses) {
                if (classHashMap != null && classHashMap.containsKey(className) && classHashMap.get(className).containsKey(styleAttributeName)) {
                    return classHashMap.get(className).get(styleAttributeName);
                }
            }
        }

        if (elementStyleMap != null && elementStyleMap.containsKey(elementType) && elementStyleMap.get(elementType).containsKey(styleAttributeName)) {
            return elementStyleMap.get(elementType).get(styleAttributeName);
        }

        if (extraStyleSet != null) {
            return extraStyleSet.getStyle(styleAttributeName, elementType, elementClasses);
        }

        return null;
    }

	private StyleSet() { }

    private void addStyleAttributesToMap(String attributeValue, HashMap<String, String> attributeStyleMap) {
        String[] styles = attributeValue.split(";");
        for (String s : styles) {
            String[] style = s.split(":");
            if (style.length == 2) {
                attributeStyleMap.put(style[0], style[1]);
            }
        }
    }

    private void parseStyleTag(String styleTagValue) {

        if (TextUtils.isEmpty(styleTagValue)) {
            return;
        }
        // Split into separate elements

        Pattern regex = Pattern.compile("([^\\{]+)\\s*\\{\\s*([^\\}]+)\\s*\\}");
        Matcher matcher = regex.matcher(styleTagValue);

        while (matcher.find()) {
            if (matcher.groupCount() == 2) {
                HashMap<String, String> attrMap = new HashMap<>();
                addStyleAttributesToMap(matcher.group(2), attrMap);
                if (matcher.group(1).startsWith(".")) {
                    // Add to the map. Remove the dot in the class name
                    classHashMap.put(TextUtils.substring(matcher.group(1), 1, matcher.group(1).length()), attrMap);
                } else {;
                    elementStyleMap.put(matcher.group(1), attrMap);
                }
            }
        }
    }

    private void mergeStyleSet(StyleSet styleSet) {
        extraStyleSet = styleSet;
    }

    static class Builder {
        private String attributeValue;
        private String styleTagValue;
        private StyleSet mergeStyleSet;

        protected Builder setAttributeValue(String attributeValue) {
            this.attributeValue = attributeValue;
            return this;
        }

        protected Builder setStyleTagValue(String styleTagValue) {
            this.styleTagValue = styleTagValue;
            return this;
        }

        protected Builder applyStyleSet(StyleSet styleSet) {
            this.mergeStyleSet = styleSet;
            return this;
        }

        protected StyleSet build() {
            StyleSet styleSet = new StyleSet();

            if (!TextUtils.isEmpty(attributeValue)) {
                styleSet.addStyleAttributesToMap(attributeValue, styleSet.attributeStyleMap);
            }

            if (mergeStyleSet != null) {
                styleSet.mergeStyleSet(mergeStyleSet);
            }

            if (!TextUtils.isEmpty(styleTagValue)) {
                styleSet.parseStyleTag(styleTagValue);
            }

            return styleSet;
        }
    }
}
