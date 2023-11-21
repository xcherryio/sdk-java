# xCherry Java SDK

[![Coverage Status](https://codecov.io/github/xcherryio/java-sdk/coverage.svg?branch=main)](https://app.codecov.io/gh/xcherryio/sdk-java/branch/main)
[![Build status](https://github.com/xcherryio/sdk-java/actions/workflows/ci-integ-test.yml/badge.svg?branch=main)](https://github.com/xcherryio/sdk-java/actions/workflows/ci-integ-test.yml)

Java SDK for [xCherry](https://github.com/xcherryio/xcherry)

## Requirements

- Java 1.8+

# Development Plan

## 1.0

- [ ] StartProcessExecution API
  - [x] Basic
  - [ ] ProcessIdReusePolicy
  - [ ] Process timeout
  - [ ] Retention policy after closed
- [ ] Executing `wait_until`/`execute` APIs
  - [x] Basic
  - [x] Parallel execution of multiple states
  - [ ] StateOption: WaitUntil/Execute API timeout and retry policy
  - [ ] AsyncState failure policy for recovery
- [ ] StateDecision
  - [x] Single next State
  - [x] Multiple next states
  - [x] Force completing process
  - [ ] Graceful completing process
  - [ ] Force fail process
  - [ ] Dead end
  - [ ] Conditional complete process with checking queue emptiness
- [ ] Commands
  - [ ] AnyOfCompletion and AllOfCompletion waitingType
  - [ ] TimerCommand
- [ ] LocalQueue
  - [ ] LocalQueueCommand
  - [ ] MessageId for deduplication
  - [ ] SendMessage API without RPC
- [ ] LocalAttribute persistence
  - [ ] LoadingPolicy (attribute selection + locking)
  - [ ] InitialUpsert
- [ ] GlobalAttribute persistence
  - [ ] LoadingPolicy (attribute selection + locking)
  - [ ] InitialUpsert
  - [ ] Multi-tables
- [ ] RPC
- [ ] API error handling for canceled, failed, timeout, terminated
- [ ] StopProcessExecution API
- [ ] WaitForStateCompletion API
- [ ] ResetStateExecution for operation
- [ ] DescribeProcessExecution API
- [ ] WaitForProcessCompletion API
- [ ] History events for operation/debugging

## Future

- [ ] Skip timer API for testing/operation
- [ ] Dynamic attributes and queue definition
- [ ] State options overridden dynamically
- [ ] Consume more than one messages in a single command with FIFO/BestMatch policies
- [ ] WaitingType: AnyCombinationsOf
- [ ] GlobalQueue
- [ ] CronSchedule
- [ ] Batch operation
- [ ] DelayStart
- [ ] Caching (with Redis, etc)
- [ ] Custom Database Query
- [ ] SearchAttribute (with ElasticSearch, etc)
- [ ] ExternalAttribute (with S3, Snowflake, etc)
