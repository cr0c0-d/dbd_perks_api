# Dead by Daylight 데이터 크롤러 API
<!--
## 프론트엔드 GitHub
https://github.com/many-yun/dbd-random-perk

## 프론트엔드 배포 페이지
https://dbd-random-perks-kr.netlify.app/
-->
<!--프로젝트 버튼-->
<!--백엔드 깃허브-->

<!--[![request-back-github-page]][back-github-page-url] -->

<!-- 백엔드 레포지토리 배지-->

![Back-Repository Commits][back-repository-commit-activity] ![Back-Repository Size][back-repository-size-shield]

<br/>

<!--목차-->

# 목차

- [[1] 프로젝트 소개](#1-프로젝트-소개)
    - [제작 동기](#제작-동기)
    - [특징](#특징)
    - [기술 스택](#기술-스택)
- [[2] 구현 기능](#2-구현-기능)
    - [데이터 크롤링](#데이터-크롤링)
    - [API 제공](#API-제공)
    - [데이터 변동 알림](#데이터-변동-알림)
- [[3] 연락처](#4-연락처)

<br/>

# [1] 프로젝트 소개

이 프로젝트는 게임 'Dead by Daylight' 관련 데이터를 자동으로 크롤링하여, 이를 API 형태로 제공하는 Spring Boot 기반의 서버입니다. 

프론트엔드 개발자가 기존에 수작업으로 '나무위키'에서 데이터를 복사하여 JSON 파일로 관리하던 방식에서 벗어나, 효율적이고 자동화된 데이터 관리를 가능하게 합니다.
<br/>

## 제작 동기

'Dead by Daylight'는 많은 팬층을 가진 인기 게임으로, 관련 정보의 수요가 높습니다. 

하지만 기존의 데이터 수집 방식은 시간과 노력이 많이 소모되었고, 최신 정보를 반영하기 어려웠습니다. 

이를 해결하기 위해, '나무위키'에서 데이터를 자동으로 크롤링하고 이를 데이터베이스에 저장하여, 프론트엔드에서 쉽게 접근할 수 있는 API 서버를 개발하게 되었습니다. 

이를 통해 사용자들은 최신 정보가 반영된 사이트를 실시간으로 이용할 수 있습니다.

<br/>

## 특징

### 크롤링

매일 자정에 게임 데이터(살인마, 생존자, 기술, 공물, 아이템, 애드온 정보) 크롤링을 수행합니다.


<br />

### API 제공
프론트엔드에서 요청 시, 저장된 데이터를 JSON 형식으로 응답합니다.

<br />

### 데이터 변동 알림
크롤링 수행 후 기존 데이터 개수에 변동이 생길 경우, Spring Boot Starter Mail을 사용해 지정한 메일 주소로 데이터 변동 알림 메일을 발송합니다. 
이를 통해 개발자가 즉시 변동 사항을 인지할 수 있습니다.

## 기술 스택

- ![java]
- ![spring-boot]
- ![spring-data-jpa]
- ![spring-boot-starter-mail]
- ![jsoup]
- ![selenium]


- ![데이터베이스](https://img.shields.io/badge/데이터베이스-8A2BE2) ![mySql]

- ![배포](https://img.shields.io/badge/배포-8A2BE2) ![aws]

 <br/>

# [2] 구현 기능

## 데이터 크롤링

- 정적 크롤링과 HTML 파싱은 Jsoup, 동적 크롤링은 Selenium 이용
- 한글 정보는 '나무위키'에서, 영문 정보는 'Dead By Daylight Fandom Wiki'에서 크롤링 수행
- '나무위키' 페이지의 마지막 수정일 확인 후, 데이터베이스에 저장된 마지막 크롤링 시의 문서 수정일자와 다를 경우 크롤링 수행

## API 제공
- 프론트엔드에서 요청 시 데이터 응답

## 데이터 변동 알림
- 크롤링 전후 데이터 개수를 비교하여 변동사항 존재시 메일 알림

<br />

<br/>

# [3] 연락처

- 📧 hyde69ciel@gmail.com
- 📋 [https://cr0c0.tistory.com/](https://cr0c0.tistory.com/)

<!--Url for Badges-->

<!-- 프론트엔드 레포지토리 정보 배지 -->

[front-repository-size-shield]: https://img.shields.io/github/repo-size/cr0c0-d/onulmohaji_f?labelColor=D8D8D8&color=BE81F7
[front-repository-commit-activity]: https://img.shields.io/github/commit-activity/t/cr0c0-d/onulmohaji_f

<!-- 백엔드 레포지토리 정보 배지 -->

[back-repository-size-shield]: https://img.shields.io/github/repo-size/cr0c0-d/onulmohaji?labelColor=D8D8D8&color=BE81F7
[back-repository-commit-activity]: https://img.shields.io/github/commit-activity/t/cr0c0-d/onulmohaji

<!-- 데모 사이트 -->

[readme-eng-shield]: https://img.shields.io/badge/-readme%20in%20english-2E2E2E?style=for-the-badge
[view-demo-shield]: https://img.shields.io/badge/-%F0%9F%98%8E%20view%20demo-F3F781?style=for-the-badge
[view-demo-url]: https://cr0c0-d.github.io
[report-bug-shield]: https://img.shields.io/badge/-%F0%9F%90%9E%20report%20bug-F5A9A9?style=for-the-badge
[report-bug-url]: https://github.com/cr0c0-d/onulmohaji_f/issues
[view-demo-eatingbookscroco]: https://img.shields.io/badge/%F0%9F%92%BB%20%EB%8D%B0%EB%AA%A8%20%EC%82%AC%EC%9D%B4%ED%8A%B8%20%EB%B3%B4%EB%9F%AC%EA%B0%80%EA%B8%B0-skyblue?style=for-the-badge
[demo-url-eatingbookscroco]: https://eatingbooks.dandycr0c0.site/search

<!--Url for Buttons-->

[request-front-github-page]: https://img.shields.io/badge/%F0%9F%93%83%20%ED%94%84%EB%A1%A0%ED%8A%B8%EC%97%94%EB%93%9C%20GitHub%20%ED%8E%98%EC%9D%B4%EC%A7%80-A9D0F5?style=for-the-badge
[request-back-github-page]: https://img.shields.io/badge/%F0%9F%93%83%20%EB%B0%B1%EC%97%94%EB%93%9C%20GitHub%20%ED%8E%98%EC%9D%B4%EC%A7%80-D6C7ED?style=for-the-badge
[front-github-page-url]: https://github.com/cr0c0-d/onulmohaji_f
[back-github-page-url]: https://github.com/cr0c0-d/onulmohaji

<!-- 기술 스택 배지 -->

[java]: https://img.shields.io/badge/Java-17-blue
[spring-boot]: https://img.shields.io/badge/Spring%20Boot-3.0.4-blue
[spring-data-jpa]: https://img.shields.io/badge/Spring%20Data%20JPA-3.2.5-blue
[jsoup]: https://img.shields.io/badge/Jsoup-1.17.2-blue
[selenium]: https://img.shields.io/badge/Selenium-4.24.0-blue
[spring-boot-starter-mail]: https://img.shields.io/badge/Spring%20Boot%20Starter%20Mail-3.2.5-blue

<!-- 기타 -->
[mySql]: https://img.shields.io/badge/MySQL-8.0-blue
[aws]: https://img.shields.io/badge/AWS%20Elastic%20Beanstalk-4F4F4F?logo=amazonaws

<!--URLS-->

[license-url]: LICENSE.md
[contribution-url]: CONTRIBUTION.md
[readme-eng-url]: ../README.md
