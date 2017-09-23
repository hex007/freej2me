/*
	This file is part of FreeJ2ME.

	FreeJ2ME is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	FreeJ2ME is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with FreeJ2ME.  If not, see http://www.gnu.org/licenses/
*/
package javax.wireless.messaging;

public interface MultipartMessage extends Message
{

	public boolean addAddress(String type, String address);

	public void addMessagePart(MessagePart part);

	public String getAddress();

	public String[] getAddresses(String type);

	public String getHeader(String headerField);

	public MessagePart getMessagePart(String contentID);

	public MessagePart[] getMessageParts();

	public String getStartContentId();

	public String getSubject();

	public boolean removeAddress(String type, String address);

	public void removeAddresses();

	public void removeAddresses(String type);

	public boolean removeMessagePart(MessagePart part);

	public boolean removeMessagePartId(String contentID);

	public boolean removeMessagePartLocation(String contentLocation);

	public void setAddress(String addr);

	public void setHeader(String headerField, String headerValue);

	public void setStartContentId(String contentId);

	public void setSubject(String subject);

}
