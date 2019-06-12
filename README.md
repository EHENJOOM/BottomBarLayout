# 安卓底部导航栏

[![](https://jitpack.io/v/EHENJOOM/BottomBarLayout.svg)](https://jitpack.io/#EHENJOOM/BottomBarLayout)

最近我的一个项目用到底部导航栏，本来我是在GitHub上找了一个功能比较强大的依赖库，但是禁止ViewPager滑动和监听ViewPager左右滑动方向这样的需求却满足不了。无奈之下，我阅读了该项目的源码，在原项目的基础上进行了二次封装，使得该底部导航栏使用的ViewPager拥有以上两个功能。下面是原项目的示例图：

![](https://github.com/chaychan/BottomBarLayout/blob/master/intro_img/display1.gif?raw=true)

![](https://github.com/chaychan/BottomBarLayout/blob/master/intro_img/4.png?raw=true)

## 使用方法：

### 1.添加依赖

首先，在build.gradle文件下加入 maven {url 'https://jitpack.io'}

```javascript
allprojects {
	repositories {
		google()
		jcenter()
		maven {url "https://jitpack.io"}
	}
}
```

然后在dependencies下加入依赖

```js
implementation 'com.github.EHENJOOM:BottomBarLayout:1.0.0'
```

### 2.底部导航栏配置

由于原项目底部导航栏功能强大，配置步骤繁杂，因此给出[原项目地址](https://github.com/chaychan/BottomBarLayout)，读者前往原项目查看方法即可，本项目方法完全兼容原项目。

本项目已继承原项目所有方法，因此不用再加入原项目依赖。

### 3.禁止ViewPager滑动

如果有禁止ViewPager滑动的需求，继承ViewPager，重写onTouchEvent(MotionEvent)和onInterceptTouchEvent(MotionEvent)方法即可。若无，使用普通ViewPager即可。

```java
package com.concise.bottombar;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class NoScrollViewPager extends ViewPager {

    public boolean isCanScroll = true;

    public NoScrollViewPager(Context context) {
        this(context, null);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCanScroll(boolean isCanScroll) {
        this.isCanScroll = isCanScroll;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isCanScroll) {
            return false;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!isCanScroll) {
            return false;
        }
        return super.onInterceptTouchEvent(event);
    }
}
```

然后在布局代码里用该NoScrollViewPager替代原来的ViewPager，并在代码里实例化即可。

```java
BottomBarLayout<NoScrollViewPager> bottomBarLayout=findViewById(R.id.bbl);

NoScrollViewPager viewPager=findViewById(R.id.viewpager);
viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(),fragmentList));
viewPager.setCanScroll(false);
bottomBarLayout.setViewPager(viewPager);
```

### 4.监听ViewPager的左右滑动

```java
bottomBarLayout.setOnDierectionListener(new BottomBarLayout.OnDirectionListener() {
    @Override
    public void onLeft(int position) {
        Log.d("TAG","左滑 "+position);
    }

    @Override
    public void onRight(int position) {
        Log.d("TAG","右滑 "+position);
    }
});
```

#### 注意：

#### 	1.左滑时的position为滑动结束前页面的值，右滑时的position值为滑动结束后页面的值。

#### 	2.如果有上述需求，在布局文件和java代码文件里引用com.concise.bottombar对应的BottomBarLayout。

感谢原项目作者：[chaychan](https://github.com/chaychan/BottomBarLayout)

感谢大家的支持。欢迎大家Star。

[本项目地址](https://github.com/EHENJOOM/BottomBarLayout/tree/1.0.0)