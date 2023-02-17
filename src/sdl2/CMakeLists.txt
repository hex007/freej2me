cmake_minimum_required(VERSION 3.1)

project(sdl_interface CXX)

find_package(SDL2 REQUIRED CONFIG REQUIRED COMPONENTS SDL2)
find_package(SDL2 REQUIRED CONFIG COMPONENTS SDL2main)

include_directories(${SDL2_INCLUDE_DIRS})

find_path(FreeImage_INCLUDE_DIR FreeImage.h HINTS ${FREEIMAGE_DIR})
find_library(FreeImage_LIBRARY NAMES FreeImage HINTS ${FREEIMAGE_DIR})

include_directories(${FreeImage_INCLUDE_DIR})

add_executable(sdl_interface anbu.cpp)

target_link_libraries(sdl_interface ${SDL2_LIBRARIES})
target_link_libraries(sdl_interface ${FreeImage_LIBRARY})

install(
    TARGETS sdl_interface
    RUNTIME DESTINATION bin
)
