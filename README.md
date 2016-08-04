# TabLayout
Imitate WeChat6.0 TabLayout

![tablayout.gif](https://github.com/Ahaochan/TabLayout/blob/master/tablayout.gif)

基于Xfermode实现的一个渐变色的TabLayout,与ViewPager无强关联

# build
```
git clone https://github.com/Ahaochan/TabLayout
cd TabLayout
./gradlew app:assembleDebug
```

## Maven
```
<dependency>
  <groupId>com.ahaochan</groupId>
  <artifactId>TabLayout</artifactId>
  <version>0.0.2</version>
  <type>pom</type>
</dependency>
```
## Gradle
```
compile 'com.ahaochan:TabLayout:0.0.2'
```
## Ivy
```
<dependency org='com.ahaochan' name='TabLayout' rev='0.0.2'>
  <artifact name='$AID' ext='pom'></artifact>
</dependency>
```

# use
```
<ViewGroup ...

	<android.support.v4.view.ViewPager
        android:id="@+id/view_pager"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"/>
		
    <com.ahao.tablayout.ui.TabLayout
        android:id="@+id/tab_layout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:textSize="12sp"
        app:textGravity="bottom"
        app:visibleCount="3"
        app:indicatorColor="@color/colorAccent"/>
</ViewGroup>
```

```
tabLayout.setOnTabClickListener(new OnTabClickListener() {
    @Override
        public void OnTabClick(View view, int position) {
			viewPager.setCurrentItem(position);
        }
    });
viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        tabLayout.scrollToTab(position, positionOffset);
        }
    });
```