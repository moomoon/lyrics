# Lyrics  [![](https://jitpack.io/v/moomoon/lyrics.svg)](https://jitpack.io/#moomoon/lyrics)
.LRC file parser

## Usage
```java
  FileLineReader reader = FileLineReader.fromStream(getAssets().open("lyric.lrc"));
  LyricGroup group = LyricGroup.builder().addItems(reader.parse(LyricItem.LineParser.Instance), LyricGroup.Policy.PickFirst).build();
```

## Installation
Available via [JitPack](https://jitpack.io/).

### Step 1.
Add it in your root build.gradle at the end of repositories:
```groovy
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
### Step 2. 
Add the dependency
```groovy
	dependencies {
	        compile 'com.github.moomoon:lyrics:1.0.2'
	}
```

