package main

import (
  "fmt"
  "time"
)

func f(left, right chan int) {
  left <- 1 + <-right
}

func main() {
  const n = 100000
  leftmost := make(chan int)
  right := leftmost
  left := leftmost
  for i := 0; i < n; i++ {
      right = make(chan int)
      go f(left, right)
      left = right
  }

  start := time.Now()
  go func(c chan int) { c <- 1 }(right)
  fmt.Println(<-leftmost)
  fmt.Println(time.Now().Sub(start))
}
