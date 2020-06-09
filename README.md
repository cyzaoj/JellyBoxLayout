## JellyBoxLayout - An Android JellyBoxLayout Library, now in Kotlin!

一个 Android 验证码输入框控件。

[![Platform](https://img.shields.io/badge/platform-android-green.svg)](http://developer.android.com/index.html)
[![Download](https://api.bintray.com/packages/aboust/android/jellyboxlayout/images/download.svg)](https://bintray.com/aboust/android/jellyboxlayout/_latestVersion)
[![API](https://img.shields.io/badge/API-14%2B-orange.svg?style=flat)](https://img.shields.io/github/license/cyzaoj/JellyBoxLayout) 
[![license](https://img.shields.io/github/license/cyzaoj/JellyBoxLayout)](https://img.shields.io/github/license/cyzaoj/JellyBoxLayout) 


<BR>
##DEMO

![效果预览图](./screenshot/device-2020-06-09-102655.png)
<BR>
<BR>

## Configure

#### Maven
```xml
<dependency>
  <groupId>com.aboust.jelly</groupId>
  <artifactId>jellyboxlayout</artifactId>
  <version>${latestVersion}</version>
  <type>pom</type>
</dependency>
```

#### JCenter

First. add to project build.gradle

``` gradle
repositories {
    jcenter()
}
```

Second. add to module build.gradle

```gradle
implementation "com.justkiddingbaby:vercodeedittext:${latestVersion}"
```

## Attributes instruction
attribute|instruction|value
---|---|---
[box_figures](library/src/main/res/values/attrs.xml)|输入框个数|integer
[box_input_type](library/src/main/res/values/attrs.xml)|number, numberPassword, text ,textPassword|enum
[box_size](library/src/main/res/values/attrs.xml)|输入框宽高设置(目前为正方形)|dimension,reference
[box_text_color](library/src/main/res/values/attrs.xml)|输入框字体颜色|color,reference
[typeface_assets](library/src/main/res/values/attrs.xml)|输入框内字体样式(从assets获取)|string
[box_text_size](library/src/main/res/values/attrs.xml)|输入框内字体大小|dimension,reference
[box_background](library/src/main/res/values/attrs.xml)|输入框背景设置|reference
[box_cursor_drawable](/vcedittext-lib/src/main/res/values/attrs.xml)|输入框光标设置|reference
[box_spacing](/vcedittext-lib/src/main/res/values/attrs.xml)|输入框间距设置|dimension,reference

<BR>
## Usage

use in the layout


``` xml

    <com.aboust.jellybox.JellyBoxLayout
        app:box_figures="4"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />
        
```

