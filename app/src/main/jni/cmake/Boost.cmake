# SPDX-FileCopyrightText: 2015 - 2024 Rime community
#
# SPDX-License-Identifier: GPL-3.0-or-later

set(BOOST_VERSION 1.89.0)

if(NOT EXISTS "${CMAKE_SOURCE_DIR}/boost/boost/algorithm")
  set(BOOST_ARCHIVE "boost_${BOOST_VERSION}.tar.bz2")
  if(NOT EXISTS "${BOOST_ARCHIVE}")
    message(STATUS "Downloading Boost ${BOOST_VERSION} ......")
    file(
      DOWNLOAD
      "https://archives.boost.io/release/${BOOST_VERSION}/source/boost_${BOOST_VERSION}.tar.bz2"
      "${BOOST_ARCHIVE}"
      SHOW_PROGRESS)
  endif()

  message(STATUS "Extracting Boost ${BOOST_VERSION} ......")
  file(ARCHIVE_EXTRACT INPUT "${BOOST_ARCHIVE}" DESTINATION
       "${CMAKE_SOURCE_DIR}")
  file(RENAME "boost_${BOOST_VERSION}" boost)
endif()

set(BOOST_INCLUDE_LIBRARIES
    algorithm
    crc
    dll
    interprocess
    range
    regex
    scope_exit
    signals2
    utility
    uuid)

if(EXISTS "${CMAKE_SOURCE_DIR}/boost/CMakeLists.txt")
  add_subdirectory(boost EXCLUDE_FROM_ALL)
else()
  foreach(__lib IN LISTS BOOST_INCLUDE_LIBRARIES)
    if(NOT TARGET Boost::${__lib})
      add_library(Boost::${__lib} INTERFACE IMPORTED)
      target_include_directories(Boost::${__lib} INTERFACE "${CMAKE_SOURCE_DIR}/boost")
    endif()
  endforeach()
endif()
