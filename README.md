# xdb-java-sdk

[![Coverage Status](https://codecov.io/github/xdblab/xdb-java-sdk/coverage.svg?branch=main)](https://app.codecov.io/gh/xdblab/xdb-java-sdk/branch/main)
[![Build status](https://github.com/xdblab/xdb-java-sdk/actions/workflows/ci-test.yml/badge.svg?branch=main)](https://github.com/xdblab/xdb-java-sdk/actions/workflows/ci-test.yml)

Java SDK for [xdb](https://github.com/xdblab/xdb)

## Requirements

- Java 1.8+

# Development Plan

## 1.0

- [ ] StartProcessExecution API
  - [ ] Basic
  - [ ] ProcessIdReusePolicy
  - [ ] Process timeout
  - [ ] Retention policy after closed
- [ ] Executing `wait_until`/`execute` APIs
  - [] Basic
  - [] Parallel execution of multiple states
  - [ ] StateOption: WaitUntil/Execute API timeout and retry policy
  - [ ] AsyncState failure policy for recovery
- [ ] StateDecision
  - [ ] Single next State
  - [ ] Multiple next states
  - [ ] Force completing process
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
