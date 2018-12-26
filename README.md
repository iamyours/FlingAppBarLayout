# FlingAppBarLayout
A Custom AppBarLayout that can fling smoothly

## ScreenShot
 <img src="./screenshot/1.gif" width="33%"><img src="./screenshot/2.gif" width="33%"><img src="./screenshot/3.gif" width="33%">
 
## Usage
#### for NestedScrollView or RecyclerView,just add behavior @string/fling_behavior
``` xml
<?xml version="1.0" encoding="utf-8"?>
<android.support.design.widget.CoordinatorLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <io.github.iamyours.flingappbarlayout.AppBarLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:elevation="0dp">

        <android.support.design.widget.CollapsingToolbarLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:padding="0dp"
            android:minWidth="44dp"
            app:layout_scrollFlags="scroll">

            <ImageView
                android:id="@+id/img1"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:background="#fff"
                />
        </android.support.design.widget.CollapsingToolbarLayout>

        <ImageView
            android:id="@+id/img_tab"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:background="#ccc"
            app:layout_scrollFlags="exitUntilCollapsed|enterAlways|enterAlwaysCollapsed" />
    </io.github.iamyours.flingappbarlayout.AppBarLayout>

    <android.support.v4.widget.NestedScrollView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:layout_behavior="@string/fling_behavior">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical">

            <ImageView
                android:id="@+id/img2"
                android:layout_width="match_parent"
                android:layout_height="wrap_content" />
            <ImageView
                android:id="@+id/img3"
                android:layout_width="match_parent"
                android:layout_height="wrap_content" />
        </LinearLayout>
    </android.support.v4.widget.NestedScrollView>
</android.support.design.widget.CoordinatorLayout>
```
#### for ViewPager add behavior @string/fling_behavior at first,
``` xml
<?xml version="1.0" encoding="utf-8"?>
<android.support.design.widget.CoordinatorLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <io.github.iamyours.flingappbarlayout.AppBarLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:nestedScrollingEnabled="true">

        <android.support.design.widget.CollapsingToolbarLayout
            android:layout_width="match_parent"
            android:layout_height="450dp"
            android:minWidth="44dp"
            app:layout_scrollFlags="scroll">

            <TextView
                android:layout_width="match_parent"
                android:layout_height="44dp"
                android:gravity="center"
                android:text="标题"
                android:textSize="18dp" />

            <ImageView
                android:layout_width="80dp"
                android:layout_height="80dp"
                android:layout_gravity="center"
                android:layout_marginTop="40dp"
                android:src="@drawable/ic_launcher_background" />
        </android.support.design.widget.CollapsingToolbarLayout>

        <android.support.design.widget.TabLayout
            android:id="@+id/tabLayout"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:background="#FFFFFF"
            app:tabGravity="fill"
            app:tabIndicatorColor="#f00"
            app:tabMode="fixed"
            app:tabSelectedTextColor="@color/colorAccent"
            app:tabTextColor="#000000" />
    </io.github.iamyours.flingappbarlayout.AppBarLayout>

    <android.support.v4.view.ViewPager
        android:id="@+id/viewPager"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:layout_behavior="@string/fling_behavior" />
</android.support.design.widget.CoordinatorLayout>
```
then in your fragment layout,add tag "fling" to NestedSrollView or RecyclerView,
``` xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">
    <android.support.v4.widget.NestedScrollView
        android:layout_width="match_parent"
        android:tag="fling"
        android:layout_height="match_parent">
        <LinearLayout
            android:layout_width="match_parent"
            android:orientation="vertical"
            android:layout_height="wrap_content">
            <ImageView
                android:id="@+id/img2"
                android:layout_width="match_parent"
                android:layout_height="wrap_content" />
            <ImageView
                android:id="@+id/img3"
                android:layout_width="match_parent"
                android:layout_height="wrap_content" />
        </LinearLayout>
    </android.support.v4.widget.NestedScrollView>
</LinearLayout>
```
or
``` xml
<?xml version="1.0" encoding="utf-8"?>
<android.support.v7.widget.RecyclerView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:id="@+id/recyclerView"
    android:layout_height="wrap_content"
    android:tag="fling"/>
```
