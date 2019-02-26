/*
 * Copyright 2018 Lars Edenbrandt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package se.nimsa.dicom

import akka.NotUsed
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Source}
import akka.util.ByteString
import se.nimsa.dicom.data.DicomParts.DicomPart
import se.nimsa.dicom.data.Elements

import scala.concurrent.duration.{DurationInt, FiniteDuration}
import scala.concurrent.{Await, ExecutionContext, Future}

package object streams {

  import ElementFlows.elementFlow
  import ElementSink.elementSink
  import ParseFlow.parseFlow

  type PartFlow = Flow[DicomPart, DicomPart, NotUsed]

  def toElements(source: Source[ByteString, Any])(implicit ec: ExecutionContext, mat: ActorMaterializer): Future[Elements] =
    source
      .via(parseFlow)
      .via(elementFlow)
      .runWith(elementSink)

  def toElementsBlocking(source: Source[ByteString, Any], d: FiniteDuration = 10.seconds)(implicit ec: ExecutionContext, mat: ActorMaterializer): Elements =
    Await.result(toElements(source), d)

}
